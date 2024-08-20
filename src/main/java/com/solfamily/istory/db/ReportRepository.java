package com.solfamily.istory.db;

import com.solfamily.istory.model.entity.FamilyMissionEntity;
import com.solfamily.istory.model.entity.ReportEntity;
import com.solfamily.istory.model.entity.id.ReportEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<ReportEntity, ReportEntityId> {
}
