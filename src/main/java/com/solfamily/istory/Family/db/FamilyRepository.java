package com.solfamily.istory.Family.db;

import com.solfamily.istory.Family.model.FamilyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyRepository extends JpaRepository<FamilyEntity, String> {

    String findRepresentiveIdByFamilyKey(String familyKey);
}
