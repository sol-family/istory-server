package com.solfamily.istory.mission.service;

import com.solfamily.istory.mission.db.MissionRepository;
import com.solfamily.istory.mission.db.user.UserRepository;
import com.solfamily.istory.mission.model.entity.MissionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MissionService{
    @Autowired
    private MissionRepository missionRepository;
    @Autowired
    private UserRepository userRepository;

    public MissionEntity save(String contents) {
        MissionEntity missionEntity = new MissionEntity();
        missionEntity.setContents(contents);
        return missionRepository.save(missionEntity);
    }

    public List<MissionEntity> findAll() {
        return missionRepository.findAll();
    }

    public MissionEntity getMissionById(Long missionNo) {
        return missionRepository.findById(missionNo).orElse(null);
    }

    public Map<String, Object> getWeeklyMission(String userId) {
        String familyKey = userRepository.getReferenceById(userId).getFamilyKey();
        String date = LocalDateTime.now().toString().substring(0,10);

        FamilyMissionEntity missionEntity = familyMissionService.getWeeklyFamilyMission(date, familyKey);
        FamilyMissionDto weeklyMission = new FamilyMissionDto();

        weeklyMission.setFamilymissionNo(missionEntity.getFamilymissionNo());

        // 미션 내용 구하기
        MissionEntity mission = missionService.getMissionById(missionEntity.getMissionNo());
        weeklyMission.setMissionNo(mission.getMissionNo());
        weeklyMission.setMissionContents(mission.getContents());

        weeklyMission.setFamilyKey(familyKey);

        weeklyMission.setRegistDate(missionEntity.getRegistDate());
        weeklyMission.setExpirationDate(missionEntity.getExpirationDate());


        // 가족 구성원
        List<UserDto> member = userService.getMemberByFamilyKey(familyKey);
        weeklyMission.setMember(member);

        // 미션 주차 구하기
        int weeklyNum = familyMissionService.getWeekNum(date, familyKey);

        // 가족미션 인스턴스만 생성됐을 때
        int order = missionEntity.getComplete();
        Map<String, ReportDto> reports;
        // 0 : 가족미션의 소감entity 미생성, 생성 과정 진행
        if(order==0){
            reports =reportService.createReportByMember(member,missionEntity.getFamilymissionNo());
        }else{
            reports = reportService.getReportsByMember(member,missionEntity.getFamilymissionNo());
        }
        weeklyMission.setReports(reports);
        boolean showCheck = reports.get(userId).getComplete() == 1;

        Map<String,Object> response = new HashMap<>();
        response.put("weeklyNum",weeklyNum);
        response.put("weeklyMission", weeklyMission);
        response.put("showCheck",showCheck);
        return  response;
    }
}
