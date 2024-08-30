package com.solfamily.istory.user.db;

import com.solfamily.istory.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    String findUserKeyByFamilyKey(String familyKey);

    @Query(value ="select family_key from istory_user where user_id = ?1", nativeQuery = true)
    Optional<String> getFamilyKeyByUserId(String userId);

    @Query(value = "select * from istory_user where family_key = ?1", nativeQuery = true)
    Optional<List<UserEntity>> findUsersByFamilyKey(String familyKey);

    UserEntity findUserNameByUserId(String userId);

    UserEntity findFamilyKeyByUserId(String userId);
}
