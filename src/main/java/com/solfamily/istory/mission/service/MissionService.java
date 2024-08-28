package com.solfamily.istory.mission.service;

import com.google.gson.Gson;
import com.solfamily.istory.mission.db.FamilyMissionRepository;
import com.solfamily.istory.mission.db.MissionImgRepository;
import com.solfamily.istory.mission.db.ReportRepository;
import com.solfamily.istory.mission.model.dto.FamilyMissionDto;
import com.solfamily.istory.mission.model.dto.ReportDto;
import com.solfamily.istory.mission.model.entity.FamilyMissionEntity;
import com.solfamily.istory.mission.model.entity.MissionEntity;
import com.solfamily.istory.mission.model.entity.MissionImgEntity;
import com.solfamily.istory.mission.model.entity.ReportEntity;
import com.solfamily.istory.mission.model.entity.id.ReportEntityId;
import com.solfamily.istory.user.db.UserRepository;
import com.solfamily.istory.user.model.UserDto;
import com.solfamily.istory.user.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MissionService {
    @Value("${FILE_DIRECTORY}")
    private String saveFolder;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FamilyMissionRepository familyMissionRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private MissionImgRepository missionImgRepository;

    public ResponseEntity<Map> getWeeklyMission(String userId) {
        Optional<String> familyKey = userRepository.getFamilyKeyByUserId(userId);
        if (familyKey.isEmpty()) {
            return errorResponse("M101");
        }
        String date = LocalDateTime.now().toString().substring(0, 10);

        Optional<FamilyMissionEntity> familyMissionEntity = familyMissionRepository.findByRegistDateLessThanEqualAndExpirationDateGreaterThanEqualAndFamilyKey(date, date, familyKey.get());
        if (familyMissionEntity.isEmpty()) {
            return errorResponse("M102");
        }

        FamilyMissionDto weeklyMission = new FamilyMissionDto();

        weeklyMission.setFamilymissionNo(familyMissionEntity.get().getFamilymissionNo());
        MissionEntity missionEntity;
        try {
            System.out.println(getMissionMap());
            missionEntity = getMissionMap().get(familyMissionEntity.get().getMissionNo());
        }catch (Exception e){
            return errorResponse("M103");
        }
        weeklyMission.setMissionNo(missionEntity.getMissionNo());
        weeklyMission.setMissionContents(missionEntity.getContents());

        weeklyMission.setFamilyKey(familyKey.get());

        weeklyMission.setRegistDate(familyMissionEntity.get().getRegistDate());
        weeklyMission.setExpirationDate(familyMissionEntity.get().getExpirationDate());


        Optional<List<UserEntity>> userEntityList = userRepository.findUsersByFamilyKey(familyKey.get());
        if (userEntityList.isEmpty()) {
            return errorResponse("M104");
        }

        List<UserDto> member = new ArrayList<>();
        for (UserEntity entity : userEntityList.get()) {
            member.add(new UserDto(entity));
        }

        weeklyMission.setMember(member);

        Optional<Integer> temp = familyMissionRepository.getWeeklyNum(familyKey.get(), date);
        if (temp.isEmpty()) {
            return errorResponse("M105");
        }
        int weeklyNum = (temp.get()%52==0)?52:temp.get()%52;

        int order = familyMissionEntity.get().getComplete();
        Map<String, ReportDto> reports = new HashMap<>();
        if (order == 0) {
            for (UserDto user : member) {
                ReportEntity entity = new ReportEntity();
                entity.setId(new ReportEntityId(user.getUserId(), familyMissionEntity.get().getFamilymissionNo()));
                entity.setThoughts("");
                entity.setWrite_date(date);
                entity.setComplete(0);
                reportRepository.save(entity);
                reports.put(user.getUserId(), new ReportDto(entity, user));
            }
            familyMissionRepository.updateCompleteByFamilyMissionNo(1, familyMissionEntity.get().getFamilymissionNo());
        } else {
            for (UserDto user : member) {
                Optional<ReportEntity> entity = reportRepository.findById(new ReportEntityId(user.getUserId(), familyMissionEntity.get().getFamilymissionNo()));
                if (entity.isEmpty()) {
                    return errorResponse("M106");
                }
                reports.put(user.getUserId(), new ReportDto(entity.get(), user));
            }
        }
        weeklyMission.setReports(reports);
        boolean showCheck = reports.get(userId).getComplete() == 1;

        Optional<MissionImgEntity> missionImg =  missionImgRepository.findByFamilymissionNo(familyMissionEntity.get().getFamilymissionNo());
        String missionImgSystemname;
        if (missionImg.isEmpty()) {
            missionImgSystemname = "";
        }else{
            missionImgSystemname = (missionImg.get().getSystemname()==null)?"":missionImg.get().getSystemname();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("weeklyNum", weeklyNum);
        response.put("showCheck", showCheck);
        response.put("missionImg",missionImgSystemname);
        response.put("weeklyMission", weeklyMission);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map> updateReportByEntity(ReportDto report) {
        String thoughts = report.getThoughts();
        String writeDate = LocalDateTime.now().toString().replace("T", " ").substring(0, 19);
        String userId = report.getUser().getUserId();
        long familymissionNo = report.getFamilymissionNo();
        if (reportRepository.updateByUserIdAndFamilyMissionNo(thoughts, writeDate, 1, userId, familymissionNo) == 1) {
            return ResponseEntity.ok(Collections.singletonMap("result", "true"));
        } else {
            return ResponseEntity.ok(Collections.singletonMap("result", "false"));
        }
    }

    public ResponseEntity<Map> registMissionImg(long familyMissionNo, MultipartFile missionImg) {
        if (missionImg == null) {
            return errorResponse("M301");
        }
        try {
            MissionImgEntity missionImgEntity = new MissionImgEntity();
            missionImgEntity.setFamilymissionNo(familyMissionNo);
            String orgName = missionImg.getOriginalFilename();
            missionImgEntity.setOriginname(orgName);

            int lastIdx = orgName.lastIndexOf(".");
            String extension = orgName.substring(lastIdx);

            if(!(extension.equals(".png")||extension.equals(".jpg")||extension.equals(".jpeg"))){
                return errorResponse("M302");
            }

            LocalDateTime now = LocalDateTime.now();
            String time = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
            String systemname = time + UUID.randomUUID() + extension;
            missionImgEntity.setSystemname(systemname);
            String path = saveFolder+systemname;
            missionImgRepository.save(missionImgEntity);
            missionImgRepository.deleteMissionImgEntitiesNotMatchingSystemname(systemname,familyMissionNo);
            missionImg.transferTo(new File(path));

            return ResponseEntity.ok(Collections.singletonMap("result", "true"));
        }catch (Exception e){
            return ResponseEntity.ok(Collections.singletonMap("result", "false"));
        }
    }

    public ResponseEntity<Map> createMissionsByFamilyKey(String familyKey, String startDate) {
        try {
            List<MissionEntity> missionList = new ArrayList<>(getMissionMap().values());
            Collections.shuffle(missionList);
            List<MissionEntity> randomMissions = missionList.stream().limit(52).toList();

            String dateFormatType = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatType);
            Date date = simpleDateFormat.parse(startDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            String registDate = startDate;
            String endDate;
            for (MissionEntity mission : randomMissions) {
                cal.add(Calendar.DAY_OF_MONTH, 6);
                endDate = simpleDateFormat.format(cal.getTime());

                FamilyMissionEntity familyMissionEntity = new FamilyMissionEntity();
                familyMissionEntity.setFamilyKey(familyKey);
                familyMissionEntity.setMissionNo(mission.getMissionNo());
                familyMissionEntity.setFamilyKey(familyKey);
                familyMissionEntity.setRegistDate(registDate);
                familyMissionEntity.setExpirationDate(endDate);
                familyMissionEntity.setComplete(0);
                familyMissionRepository.save(familyMissionEntity);

                familyMissionRepository.save(familyMissionEntity);
                cal.add(Calendar.DAY_OF_MONTH, 1);
                registDate = simpleDateFormat.format(cal.getTime());
            }
            return ResponseEntity.ok(Collections.singletonMap("success", "true"));
        } catch (Exception e) {
            return errorResponse("");
        }
    }

    public ResponseEntity<Map> getMissionsByRound(String userId, int roundNum) {
        Optional<String> familyKey = userRepository.getFamilyKeyByUserId(userId);
        if (familyKey.isEmpty()) {
            return errorResponse("M401");
        }

        if(roundNum <= 0){
            roundNum = (int) (familyMissionRepository.countByFamilyKey(familyKey.get())/52);
        }

        Pageable  pageable = PageRequest.of(roundNum-1, 52);
        Page<FamilyMissionEntity> resultPage = familyMissionRepository.findByFamilyKeyOrderByRegistDate(familyKey.get(), pageable);

        List<FamilyMissionEntity> missions = resultPage.getContent();
        String[] temp = missions.get(0).getRegistDate().substring(0,7).split("-");
        HashMap<String,Integer> roundDate = new HashMap<>();
        roundDate.put("startYear",Integer.parseInt(temp[0]));
        roundDate.put("startMonth",Integer.parseInt(temp[1]));
        roundDate.put("endYear",Integer.parseInt(temp[0])+1);
        roundDate.put("endMonth",Integer.parseInt(temp[1]));

        String roundEndDate = missions.get(51).getExpirationDate();
        String currentDate = LocalDate.now().toString().substring(0,10);

        if (roundEndDate.compareTo(currentDate) > 0) {
            for(int i = 51;i>=0;i--){
                String date = missions.get(i).getRegistDate();
                if (date.compareTo(currentDate) > 0) {
                    missions.get(i).setComplete(3);
                }else{
                    break;
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("roundNum", roundNum);
        response.put("roundDate", roundDate);
        response.put("roundMissions", missions);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map> getMissionByWeek(String userId, int roundNum, int weekNum) {
        Optional<String> familyKey = userRepository.getFamilyKeyByUserId(userId);
        if (familyKey.isEmpty()) {
            return errorResponse("M101");
        }
        Pageable  pageable = PageRequest.of((roundNum-1)*52+weekNum, 1);
        Page<FamilyMissionEntity> resultPage = familyMissionRepository.findByFamilyKeyOrderByRegistDate(familyKey.get(), pageable);

        FamilyMissionEntity familyMissionEntity  = resultPage.getContent().get(0);
        FamilyMissionDto weeklyMission = new FamilyMissionDto();

        weeklyMission.setFamilymissionNo(familyMissionEntity.getFamilymissionNo());
        MissionEntity missionEntity;
        try {
            missionEntity = getMissionMap().get(familyMissionEntity.getMissionNo());
        }catch (Exception e){
            return errorResponse("M103");
        }
        weeklyMission.setMissionNo(missionEntity.getMissionNo());
        weeklyMission.setMissionContents(missionEntity.getContents());

        weeklyMission.setFamilyKey(familyKey.get());

        weeklyMission.setRegistDate(familyMissionEntity.getRegistDate());
        weeklyMission.setExpirationDate(familyMissionEntity.getExpirationDate());


        Optional<List<UserEntity>> userEntityList = userRepository.findUsersByFamilyKey(familyKey.get());
        if (userEntityList.isEmpty()) {
            return errorResponse("M104");
        }

        List<UserDto> member = new ArrayList<>();
        for (UserEntity entity : userEntityList.get()) {
            member.add(new UserDto(entity));
        }

        weeklyMission.setMember(member);

        int order = familyMissionEntity.getComplete();
        Map<String, ReportDto> reports = new HashMap<>();
        if (order == 0) {
            for (UserDto user : member) {
                ReportEntity entity = new ReportEntity();
                entity.setId(new ReportEntityId(user.getUserId(), familyMissionEntity.getFamilymissionNo()));
                entity.setThoughts("");
                entity.setComplete(0);
                reportRepository.save(entity);
                reports.put(user.getUserId(), new ReportDto(entity, user));
            }
            familyMissionRepository.updateCompleteByFamilyMissionNo(1, familyMissionEntity.getFamilymissionNo());
        } else {
            for (UserDto user : member) {
                Optional<ReportEntity> entity = reportRepository.findById(new ReportEntityId(user.getUserId(), familyMissionEntity.getFamilymissionNo()));
                if (entity.isEmpty()) {
                    return errorResponse("M106");
                }
                reports.put(user.getUserId(), new ReportDto(entity.get(), user));
            }
        }
        weeklyMission.setReports(reports);

        Optional<MissionImgEntity> missionImg =  missionImgRepository.findByFamilymissionNo(familyMissionEntity.getFamilymissionNo());
        String missionImgSystemname;
        if (missionImg.isEmpty()) {
            missionImgSystemname = "";
        }else{
            missionImgSystemname = (missionImg.get().getSystemname()==null)?"":missionImg.get().getSystemname();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("roundNum",roundNum);
        response.put("weekNum",weekNum);
        response.put("missionImg",missionImgSystemname);
        response.put("weeklyMission",weeklyMission);
        return ResponseEntity.ok(response);
    }

    private Map<Long,MissionEntity> getMissionMap() throws Exception{
        Gson gson = new Gson();
        System.setIn(new FileInputStream("src/main/resources/missionMap.json"));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String str = null;
        StringBuilder sb = new StringBuilder();

        while ((str = br.readLine()) != null) {
            sb.append(str);
        }

        Type MissionEntityMapType = new TypeToken<Map<Long,MissionEntity>>() {
        }.getType();
        return gson.fromJson(sb.toString(), MissionEntityMapType);
    }

    private ResponseEntity<Map> errorResponse(String msg) {
        return ResponseEntity.ok(Collections.singletonMap("errorMsg", msg));
    }
}
