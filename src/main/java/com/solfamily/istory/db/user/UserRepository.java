package com.solfamily.istory.db.user;

import com.solfamily.istory.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    String findUserKeyByUserId(String userId);
}
