package com.solfamily.istory.mission.db;

import com.solfamily.istory.mission.model.entity.FamilyMissionEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FamilyMissionRepository extends JpaRepository<FamilyMissionEntity, Long> {
    Optional<FamilyMissionEntity> findByRegistDateLessThanEqualAndExpirationDateGreaterThanEqualAndFamilyKey(String date1, String date2, String familyKey);

    @Transactional
    @Modifying
    @Query("UPDATE FamilyMissionEntity f SET f.complete = ?1 WHERE f.familymissionNo = ?2")
    int updateCompleteByFamilyMissionNo(int complete, long familyMissionNo);

    long countByFamilyKey(String familyKey);

    @Query(value = "select fm from FamilyMissionEntity fm where fm.familyKey = ?1 order by fm.registDate")
    Page<FamilyMissionEntity> findByFamilyKeyOrderByRegistDate(String familyKey, Pageable pageable);


    @Query(value = "select weekly_num from ( select regist_date, expiration_date, row_number() over (order by regist_date) AS weekly_num FROM istory_familymission where family_key = ?1 ) family_missions where ?2 between regist_date and expiration_date", nativeQuery = true)
    Optional<Integer> getWeeklyNum(String familyKey,String  date);
}
