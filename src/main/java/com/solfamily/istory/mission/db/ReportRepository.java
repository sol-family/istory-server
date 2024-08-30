package com.solfamily.istory.mission.db;

import com.solfamily.istory.mission.model.entity.ReportEntity;
import com.solfamily.istory.mission.model.entity.id.ReportEntityId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReportRepository extends JpaRepository<ReportEntity, ReportEntityId> {
    @Transactional
    @Modifying
    @Query("UPDATE ReportEntity r SET r.thoughts = ?1, r.write_date = ?2, r.complete = ?3 WHERE r.id.userId = ?4 and r.id.familymissionNo = ?5")
    int updateByUserIdAndFamilyMissionNo(String thoughts, String writeDate, int complete, String userId, long familyMissionNo);

    @Query(value = "select rp from ReportEntity rp where rp.id.familymissionNo = :familymissionNo")
    List<ReportEntity> findAllByFamilyMissionNo(Long familymissionNo);
}
