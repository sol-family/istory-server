package com.solfamily.istory.db;

import com.solfamily.istory.model.entity.FamilyMissionEntity;
import com.solfamily.istory.model.entity.MissionImgEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionImgRepository extends JpaRepository<MissionImgEntity, String> {
}
