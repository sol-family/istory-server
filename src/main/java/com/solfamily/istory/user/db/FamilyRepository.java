package com.solfamily.istory.user.db;

import com.solfamily.istory.user.model.FamilyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyRepository extends JpaRepository<FamilyEntity, String> {
}
