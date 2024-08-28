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
}
