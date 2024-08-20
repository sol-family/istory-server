package com.solfamily.istory.db.user;

import com.solfamily.istory.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// 아래는 제네릭 안에 들어가는 값에 대한 설명
// 데이터베이스에서 UserEntity와 관련된 CRUD(Create, Read, Update, Delete) 작업을 처리할 수 있게 설정
// 기본 키의 타입이 Long임을 JpaRepository에 알려주는 역할
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
