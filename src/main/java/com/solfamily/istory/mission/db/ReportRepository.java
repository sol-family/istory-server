package com.solfamily.istory.mission.db;

import com.solfamily.istory.mission.model.entity.ReportEntity;
import com.solfamily.istory.mission.model.entity.id.ReportEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReportRepository extends JpaRepository<ReportEntity, ReportEntityId> {
    @Query(value = "update istory_report set thoughts=?1, write_date = now(), complete = 1 where user_id=?2 and familymission_no = ?3", nativeQuery = true)
    int updateReport(String thoughts, String userId, long familyMissionNo);
}
