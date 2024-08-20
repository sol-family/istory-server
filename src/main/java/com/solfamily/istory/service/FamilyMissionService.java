package com.solfamily.istory.service;

import com.solfamily.istory.model.entity.FamilyMissionEntity;

import java.util.Date;

public interface FamilyMissionService {
    FamilyMissionEntity getWeeklyFamilyMission(String date, String familyId);

    boolean createMissionsByFamilyId(String familyId,String startDate);
}
