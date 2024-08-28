package com.solfamily.istory.mission.db;

import com.solfamily.istory.mission.model.entity.MissionImgEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MissionImgRepository extends JpaRepository<MissionImgEntity, String> {
    Optional<MissionImgEntity> findByFamilymissionNo(long familymissionNo);

    @Modifying
    @Transactional
    @Query("DELETE FROM MissionImgEntity m WHERE m.systemname != :systemname AND m.familymissionNo = :familyMissionNo")
    void deleteMissionImgEntitiesNotMatchingSystemname(@Param("systemname") String systemname, @Param("familyMissionNo") Long familyMissionNo);
}
