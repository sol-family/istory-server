package com.solfamily.istory.userdb;

import com.solfamily.istory.usermodel.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {
}
