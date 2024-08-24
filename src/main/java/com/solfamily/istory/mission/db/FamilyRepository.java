package com.solfamily.istory.mission.db;

import com.solfamily.istory.mission.model.entity.FamilyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyRepository extends JpaRepository<FamilyEntity, String> {
}
