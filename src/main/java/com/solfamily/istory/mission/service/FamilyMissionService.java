package com.solfamily.istory.mission.service;

import com.solfamily.istory.mission.db.FamilyMissionRepository;
import com.solfamily.istory.mission.db.MissionRepository;
import com.solfamily.istory.mission.model.entity.FamilyMissionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FamilyMissionService{
    @Autowired
    private FamilyMissionRepository familyMissionRepository;

    @Autowired
    private MissionRepository missionRepository;

    public FamilyMissionEntity getWeeklyFamilyMission(String date, String familyKey) {
        return familyMissionRepository.getFamilyMissionByDate(date, familyKey);
    }

    public boolean createMissionsByFamilyKey(String familyKey, String startDate) {
        try {
            List<Long> missionIdList = missionRepository.findAllIds();
            // ID를 무작위로 섞기
            Collections.shuffle(missionIdList);
            // 52개 ID 선택 (데이터 양보다 요청이 큰 경우 전체 반환)
            List<Long> randomNos = missionIdList.stream().limit(52).toList();

            // 입력 날짜 형식
            String dateFormatType = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatType);
            Date date = simpleDateFormat.parse(startDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            String registDate = startDate;
            String endDate;
            for (Long missionNo : randomNos) {
                cal.add(Calendar.DAY_OF_MONTH, 6);
                endDate = simpleDateFormat.format(cal.getTime());
                FamilyMissionEntity familyMissionEntity = new FamilyMissionEntity();
                familyMissionEntity.setFamilyKey(familyKey);
                familyMissionEntity.setMissionNo(missionNo);
                familyMissionEntity.setRegistDate(registDate);
                familyMissionEntity.setExpirationDate(endDate);
                familyMissionEntity.setComplete(0);

                familyMissionRepository.save(familyMissionEntity);
                cal.add(Calendar.DAY_OF_MONTH, 1);
                registDate = simpleDateFormat.format(cal.getTime());
            }
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public int getWeekNum(String date, String familyKey) {
        List<FamilyMissionEntity> list = familyMissionRepository.getMissionsByFamilyKey(familyKey);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate nowDate = LocalDate.parse(date, formatter);

        for(int i = 0;i<list.size();i++){
            FamilyMissionEntity entity = list.get(i);
            LocalDate startDate = LocalDate.parse(entity.getRegistDate(), formatter);
            LocalDate endDate = LocalDate.parse(entity.getExpirationDate(), formatter);
            if((nowDate.isEqual(startDate) || nowDate.isAfter(startDate)) &&
                    (nowDate.isEqual(endDate) || nowDate.isBefore(endDate))){
                return i+1;
            }
        }
        return 0;
    }
}
