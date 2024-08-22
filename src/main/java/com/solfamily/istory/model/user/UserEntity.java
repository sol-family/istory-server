package com.solfamily.istory.model.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@Entity(name = "user")
@Table(name = "istory_user")
public class UserEntity {

    @Id
    private String userId;

    private String userPw;

    private String userName;

    private String userPhone;

    private String userGender;

    private String userBirth;

    private String userType;

    private String userKey;

    private LocalDateTime joinDate;

    private String userProfile;

    private String familyKey;

    @Transient
    private String inviteCode;
}