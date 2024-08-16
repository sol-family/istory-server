package com.solfamily.istory.controller;

import com.solfamily.istory.model.UserEntity;
import com.solfamily.istory.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @PostMapping("/save-user")
    public UserEntity save(
            @RequestBody
            UserEntity userEntity
    ) {
        return userService.save(userEntity);
    }

    @GetMapping("/find-all")
    public List<UserEntity> findAll() {
        return userService.findAll();
    }
}
