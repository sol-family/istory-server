package com.solfamily.istory.mission.db;

import com.solfamily.istory.mission.model.entity.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MissionRepository extends JpaRepository<MissionEntity, Long> {
    @Query(value="select mission_no from istory_mission",nativeQuery = true)
    List<Long> findAllIds();
}
