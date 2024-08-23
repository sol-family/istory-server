package com.solfamily.istory.user.db;

import com.solfamily.istory.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    String findUserKeyByFamilyKey(String familyKey);
}
