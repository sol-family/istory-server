package com.solfamily.istory.db;

import com.solfamily.istory.model.entity.FamilyMissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface FamilyMissionRepository extends JpaRepository<FamilyMissionEntity, Long> {
    @Query(value = "SELECT * FROM istory_familymission WHERE (?1 BETWEEN regist_date AND expiration_date) and family_id = ?2", nativeQuery = true)
    FamilyMissionEntity getFamilyMissionByDate(String date,String familyId);

}
