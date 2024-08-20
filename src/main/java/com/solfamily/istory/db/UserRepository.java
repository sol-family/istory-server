package com.solfamily.istory.db;

import com.solfamily.istory.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    @Query(value = "select istory_user where familyId = ?1", nativeQuery = true)
    List<UserEntity> findAllByFamilyId(String familyId);
}
