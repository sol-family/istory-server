package com.solfamily.istory.user.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserDto {
    private String userId;
    private String userName;
    private String userPhone;
    private String userGender;
    private String userBirth;
    private String userType;
    private String userProfile;
    private String familyKey;

    public UserDto(UserEntity entity){
        this.userId = entity.getUserId();
        this.userName = entity.getUserName();
        this.userPhone = entity.getUserPhone();
        this.userGender = entity.getUserGender();
        this.userBirth = entity.getUserBirth();
        this.userType = entity.getUserType();
        this.userProfile = entity.getUserProfile();
        this.familyKey = entity.getFamilyKey();
    }
}
