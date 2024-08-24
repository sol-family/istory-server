package com.solfamily.istory.mission.db;

import com.solfamily.istory.mission.model.entity.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MissionRepository extends JpaRepository<MissionEntity, Long> {
    @Query("SELECT m.missionNo FROM MissionEntity m")
    List<Long> findAllMissionNos();
}
