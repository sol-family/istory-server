package com.solfamily.istory.service.user;

import com.solfamily.istory.db.user.UserRepository;
import com.solfamily.istory.model.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserEntity save(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }


}
