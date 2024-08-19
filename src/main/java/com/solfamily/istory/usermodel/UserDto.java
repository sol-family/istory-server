package com.solfamily.istory.usermodel;

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
}
