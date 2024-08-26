package com.solfamily.istory.user.service;

import com.solfamily.istory.user.model.UserDto;
import com.solfamily.istory.user.model.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class UserConverterService {

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
