package com.solfamily.istory.model.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "user") // itsory db에 있는 user 테이블과 매핑
public class UserEntity {

    //id 키가 primary key로 동작하므로 @Id 사용
    // @GenerateValue를 통해 id가 어떤식으로 생성될지 설정 -> 데이터베이스마다 설정 값은 다름. MySql의 경우 GenerationType.IDENTITY 사용
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
