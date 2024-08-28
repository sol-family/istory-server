package com.solfamily.istory.user.controller;

import com.solfamily.istory.user.model.UserDto;
import com.solfamily.istory.Family.service.FamilyService;
import com.solfamily.istory.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;
    private final FamilyService familyService;

    // 회원가입
    @PostMapping("/sign-up")
    public ResponseEntity<Map<String, Object>> userJoin(
            @RequestBody
            UserDto userDto
    ) {
        return userService.signUp(userDto);
    }

    // 유저 로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> userLogin(
            @RequestParam(required = true, value = "userId")
            String userId,
            @RequestParam(required = true, value = "userPw")
            String userPw
    ) {
        return userService.userLogin(userId, userPw);
    }

    // 유저 상태 확인
    @PostMapping("/status")
    public ResponseEntity<Map<String, Object>> getUserStatus(
        HttpServletRequest request
    ) {
        return userService.getUserStatus(request);
    }

    // 단일 유저 조회
    @GetMapping("/single-inquire")
    public ResponseEntity<Map<String, Object>> getUser(
        HttpServletRequest request
    ) {
        return userService.getUser(request);
    }

    // 모든 유저 조회
    @GetMapping("/all-inquire")
    public ResponseEntity<Map<String, Object>> getAllUser() {
        return userService.getAllUser();
    }
}
