package com.solfamily.istory.user.controller;

import com.solfamily.istory.user.model.LoginRequest;
import com.solfamily.istory.user.model.UserDto;
import com.solfamily.istory.Family.service.FamilyService;
import com.solfamily.istory.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

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
        @RequestBody
        LoginRequest loginRequest
    ) {
        return userService.userLogin(loginRequest);
    }

    // 유저 상태 확인
    @GetMapping("/status")
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

    @PostMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateUserProfile(HttpServletRequest request, MultipartFile image){
        return userService.updateProfile(request,image);
    }

    @GetMapping("/account")
    public ResponseEntity<Map<String, Object>> getUserAccount(HttpServletRequest request){
        return userService.getAccountList(request);
    }

}
