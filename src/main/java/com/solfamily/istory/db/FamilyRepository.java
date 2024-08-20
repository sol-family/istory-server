package com.solfamily.istory.db;

import com.solfamily.istory.model.entity.FamilyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyRepository extends JpaRepository<FamilyEntity, String> {
}
