package com.solfamily.istory.service.Impl;

import com.solfamily.istory.db.UserRepository;
import com.solfamily.istory.model.dto.UserDTO;
import com.solfamily.istory.model.entity.UserEntity;
import com.solfamily.istory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;


    @Override
    public String getFamilyIdByUserId(String userId) {
        UserEntity user = userRepository.getReferenceById(userId);
        return user.getFamilyId();
    }

    @Override
    public List<UserDTO> getMemberByFamilyId(String familyId) {
        List<UserEntity> list = userRepository.findAllByFamilyId(familyId);
        List<UserDTO> member = new ArrayList<>();
        for (UserEntity user : list) {
            UserDTO udto = new UserDTO();
            udto.setUserId(user.getUserId());
            udto.setUserName(user.getUserName());
            udto.setPhone(user.getPhone());
            udto.setGender(user.getGender());
            udto.setBirth(user.getBirth());
            udto.setUserType(user.getUserType());
            udto.setUserProfile(user.getUserProfile());
            udto.setFamilyId(user.getFamilyId());
            member.add(udto);
        }
        return member;
    }
}
