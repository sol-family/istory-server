package com.solfamily.istory.service.user;

import com.solfamily.istory.model.user.UserDto;
import com.solfamily.istory.model.user.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class UserConverter {

    public UserDto toDto(UserEntity userEntity) {

    return UserDto.builder()
                .userId(userEntity.getUserId())
                .userName(userEntity.getUserName())
                .userPhone(userEntity.getUserPhone())
                .userGender(userEntity.getUserGender())
                .userBirth(userEntity.getUserBirth())
                .userType(userEntity.getUserType())
                .userProfile(userEntity.getUserProfile())
                .familyKey(userEntity.getFamilyKey())
                .build();
    };
}
