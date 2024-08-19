package com.solfamily.istory.userservice;

import com.solfamily.istory.usermodel.UserDto;
import com.solfamily.istory.usermodel.UserEntity;
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
                .build();
    };
}
