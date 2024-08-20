package com.solfamily.istory.service.Impl;

import com.solfamily.istory.db.FamilyMissionRepository;
import com.solfamily.istory.db.MissionRepository;
import com.solfamily.istory.model.entity.FamilyMissionEntity;
import com.solfamily.istory.service.FamilyMissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FamilyMissionServiceImpl implements FamilyMissionService {
    @Autowired
    private FamilyMissionRepository familyMissionRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Override
    public FamilyMissionEntity getWeeklyFamilyMission(String date, String familyId) {
        return familyMissionRepository.getFamilyMissionByDate(date, familyId);
    }

    @Override
    public boolean createMissionsByFamilyId(String familyId, String startDate) {
        try {
            List<Long> missionIdList = missionRepository.findAllIds();
            // ID를 무작위로 섞기
            Collections.shuffle(missionIdList);
            // 52개 ID 선택 (데이터 양보다 요청이 큰 경우 전체 반환)
            List<Long> randomIds = missionIdList.stream().limit(52).toList();

            // 입력 날짜 형식
            String dateFormatType = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatType);
            Date date = simpleDateFormat.parse(startDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            String registDate = startDate;
            String endDate;
            for (Long missionId : randomIds) {
                cal.add(Calendar.DAY_OF_MONTH, 6);
                endDate = simpleDateFormat.format(cal.getTime());

                FamilyMissionEntity familyMissionEntity = new FamilyMissionEntity();
                familyMissionEntity.setFamilyId(familyId);
                familyMissionEntity.setMissionNo(missionId);
                familyMissionEntity.setRegistDate(registDate);
                familyMissionEntity.setExpirationDate(endDate);
                familyMissionEntity.setComplete(0);
                familyMissionRepository.save(familyMissionEntity);

                cal.add(Calendar.DAY_OF_MONTH, 1);
                registDate = simpleDateFormat.format(cal.getTime());
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
