package com.solfamily.istory.user.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private String userId;

    private String userPw;

    private String userName;

    private String userPhone;

    private String userGender;

    private String userBirth;

    private String userType;

    private String userProfile;

    public UserDto(UserEntity entity){
        this.userId = entity.getUserId();
        this.userName = entity.getUserName();
        this.userPhone = entity.getUserPhone();
        this.userGender = entity.getUserGender();
        this.userBirth = entity.getUserBirth();
        this.userType = entity.getUserType();
        this.userProfile = entity.getUserProfile();
    }
}
