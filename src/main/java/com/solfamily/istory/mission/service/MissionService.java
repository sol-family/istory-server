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
import com.solfamily.istory.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

        Optional<FamilyMissionEntity> familyMissionEntity = familyMissionRepository.getFamilyMissionByDate(date, familyKey.get());
        if(familyMissionEntity.isEmpty()) {
            String errorMsg = "";
            return errorResponse(errorMsg);
        }

        FamilyMissionDto weeklyMission = new FamilyMissionDto();

        weeklyMission.setFamilymissionNo(familyMissionEntity.get().getFamilymissionNo());

        // 미션 내용 구하기
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


        // 가족 구성원
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

        // 미션 주차 구하기
        Optional<Integer> weeklyNum = familyMissionRepository.getWeeklyNum(familyKey.get(),date);
        if(weeklyNum.isEmpty()) {
            String errorMsg = "";
            return errorResponse(errorMsg);
        }

        // 가족미션 인스턴스만 생성됐을 때
        int order = familyMissionEntity.get().getComplete();
        Map<String, ReportDto> reports = new HashMap<>();
        // 0 : 가족미션의 소감entity 미생성, 생성 과정 진행
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
            familyMissionRepository.updateComplete(1,familyMissionEntity.get().getFamilymissionNo());
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
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<String> updateReportByEntitly(ReportDto report) {
        String thoughts = report.getThoughts();
        String userId = report.getUser().getUserId();
        long familymissionNo = report.getFamilymissionNo();
        if(reportRepository.updateReport(thoughts,userId,familymissionNo)==1){
            return new ResponseEntity<>("{\"success\": true}", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("{\"success\": false}", HttpStatus.OK);
        }
    }

    public boolean createMissionsByFamilyKey(String familyKey, String startDate) {
        return false;
    }

    private ResponseEntity<Map> errorResponse(String msg){
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errorMsg", msg);
        return new ResponseEntity<>(errorResponse, HttpStatus.OK);
    }
}
