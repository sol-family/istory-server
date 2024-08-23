package com.solfamily.istory.mission.db;

import com.solfamily.istory.mission.model.entity.FamilyMissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FamilyMissionRepository extends JpaRepository<FamilyMissionEntity, Long> {
    @Query(value = "SELECT * FROM istory_familymission WHERE (?1 BETWEEN regist_date AND expiration_date) and family_Key = ?2", nativeQuery = true)
    Optional<FamilyMissionEntity> getFamilyMissionByDate(String date, String familyKey);

    @Query(value ="update istory_familymission set complete = ?1 where familymission_no = ?2", nativeQuery = true)
    boolean updateComplete(int order,long familymissionNo);

    @Query(value = "SELECT * FROM istory_familymission WHERE  family_key = ?1 order by regist_date", nativeQuery = true)
    List<FamilyMissionEntity> getMissionsByFamilyKey(String familyKey);

    @Query(value = "select weekly_num from ( select regist_date, expiration_date, row_number() over (order by regist_date) AS weekNo FROM istory_familymission where family_key = ?1 ) family_missions where ?2 between regist_date and expiration_date", nativeQuery = true)
    Optional<Integer> getWeeklyNum(String familyKey,String  date);
}
