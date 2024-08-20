package com.solfamily.istory.model.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String userId;
    private String userName;
    private String phone;
    private String gender;
    private String birth;
    private String userType;
    private String userProfile;
    private String familyId;
}
