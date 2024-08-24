package com.solfamily.istory.mission.service;

import com.solfamily.istory.mission.db.FamilyMissionRepository;
import com.solfamily.istory.mission.db.MissionRepository;
import com.solfamily.istory.mission.db.ReportRepository;
import com.solfamily.istory.mission.model.dto.FamilyMissionDto;
import com.solfamily.istory.mission.model.dto.ReportDto;
import com.solfamily.istory.mission.model.entity.FamilyMissionEntity;
import com.solfamily.istory.mission.model.entity.MissionEntity;
import com.solfamily.istory.mission.model.entity.ReportEntity;
import com.solfamily.istory.mission.model.entity.id.ReportEntityId;
import com.solfamily.istory.user.db.UserRepository;
import com.solfamily.istory.user.model.UserDto;
import com.solfamily.istory.user.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class MissionService{
    @Autowired
    private MissionRepository missionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FamilyMissionRepository familyMissionRepository;
    @Autowired
    private ReportRepository reportRepository;

    public ResponseEntity<Map> getWeeklyMission(String userId) {
        Optional<String> familyKey = userRepository.getFamilyKeyByUserId(userId);
        if(familyKey.isEmpty()) {
            String errorMsg = "";
            return errorResponse(errorMsg);
        }
        String date = LocalDateTime.now().toString().substring(0,10);

        Optional<FamilyMissionEntity> familyMissionEntity = familyMissionRepository.findByRegistDateLessThanEqualAndExpirationDateGreaterThanEqualAndFamilyKey(date, date, familyKey.get());
        if(familyMissionEntity.isEmpty()) {
            String errorMsg = "";
            return errorResponse(errorMsg);
        }

        FamilyMissionDto weeklyMission = new FamilyMissionDto();

        weeklyMission.setFamilymissionNo(familyMissionEntity.get().getFamilymissionNo());

        Optional<MissionEntity> missionEntity = missionRepository.findById(familyMissionEntity.get().getMissionNo());
        if(missionEntity.isEmpty()) {
            String errorMsg = "";
            return errorResponse(errorMsg);
        }
        weeklyMission.setMissionNo(missionEntity.get().getMissionNo());
        weeklyMission.setMissionContents(missionEntity.get().getContents());

        weeklyMission.setFamilyKey(familyKey.get());

        weeklyMission.setRegistDate(familyMissionEntity.get().getRegistDate());
        weeklyMission.setExpirationDate(familyMissionEntity.get().getExpirationDate());


        Optional<List<UserEntity>> userEntityList = userRepository.findUsersByFamilyKey(familyKey.get());
        if(userEntityList.isEmpty()) {
            String errorMsg = "";
            return errorResponse(errorMsg);
        }

        List<UserDto> member = new ArrayList<>();
        for(UserEntity entity : userEntityList.get()) {
            member.add(new UserDto(entity));
        }

        weeklyMission.setMember(member);

        Optional<Integer> weeklyNum = familyMissionRepository.getWeeklyNum(familyKey.get(),date);
        if(weeklyNum.isEmpty()) {
            String errorMsg = "";
            return errorResponse(errorMsg);
        }

        int order = familyMissionEntity.get().getComplete();
        Map<String, ReportDto> reports = new HashMap<>();
        if(order==0){
            for(UserDto user : member){
                ReportEntity entity = new ReportEntity();
                entity.setId(new ReportEntityId(user.getUserId(),familyMissionEntity.get().getFamilymissionNo()));
                entity.setThoughts("");
                entity.setWrite_date(date);
                entity.setComplete(0);
                reportRepository.save(entity);
                reports.put(user.getUserId(),new ReportDto(entity,user));
            }
            familyMissionRepository.updateCompleteByFamilyMissionNo(1,familyMissionEntity.get().getFamilymissionNo());
        }else{
            for(UserDto user : member){
                Optional<ReportEntity> entity = reportRepository.findById(new ReportEntityId(user.getUserId(),familyMissionEntity.get().getFamilymissionNo()));
                if(entity.isEmpty()) {
                    String errorMsg = "";
                    return errorResponse(errorMsg);
                }
                reports.put(user.getUserId(),new ReportDto(entity.get(),user));
            }
        }
        weeklyMission.setReports(reports);
        boolean showCheck = reports.get(userId).getComplete() == 1;

        Map<String,Object> response = new HashMap<>();
        response.put("weeklyNum",weeklyNum.get());
        response.put("weeklyMission", weeklyMission);
        response.put("showCheck",showCheck);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map> updateReportByEntity(ReportDto report) {
        String thoughts = report.getThoughts();
        String writeDate = LocalDateTime.now().toString().replace("T", " ").substring(0, 19);
        String userId = report.getUser().getUserId();
        long familymissionNo = report.getFamilymissionNo();
        if(reportRepository.updateByUserIdAndFamilyMissionNo(thoughts, writeDate,1,userId,familymissionNo)==1){
            return ResponseEntity.ok(Collections.singletonMap("success", "true"));
        }else{
            return errorResponse("");
        }
    }

    public ResponseEntity<Map> createMissionsByFamilyKey(String familyKey, String startDate) {
        try {
            List<Long> missionIdList = missionRepository.findAllMissionNos();

            Collections.shuffle(missionIdList);
            List<Long> randomNos = missionIdList.stream().limit(52).toList();

            String dateFormatType = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatType);
            Date date = simpleDateFormat.parse(startDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            String registDate = startDate;
            String endDate;
            for (Long missionId : randomNos) {
                for (Long missionNo : randomNos) {
                    cal.add(Calendar.DAY_OF_MONTH, 6);
                    endDate = simpleDateFormat.format(cal.getTime());

                    FamilyMissionEntity familyMissionEntity = new FamilyMissionEntity();
                    familyMissionEntity.setFamilyKey(familyKey);
                    familyMissionEntity.setMissionNo(missionId);
                    familyMissionEntity.setFamilyKey(familyKey);
                    familyMissionEntity.setMissionNo(missionNo);
                    familyMissionEntity.setRegistDate(registDate);
                    familyMissionEntity.setExpirationDate(endDate);
                    familyMissionEntity.setComplete(0);
                    familyMissionRepository.save(familyMissionEntity);

                    familyMissionRepository.save(familyMissionEntity);
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    registDate = simpleDateFormat.format(cal.getTime());
                }
            }
            return ResponseEntity.ok(Collections.singletonMap("success", "true"));
        }catch (Exception e){
            return errorResponse("");
        }
    }

    private ResponseEntity<Map> errorResponse(String msg){
        return ResponseEntity.ok(Collections.singletonMap("errorMsg", msg));
    }

}
