package com.solfamily.istory.controller.user;

import com.solfamily.istory.model.user.UserEntity;
import com.solfamily.istory.service.user.UserService;
import lombok.RequiredArgsConstructor;
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
