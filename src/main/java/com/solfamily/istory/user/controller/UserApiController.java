package com.solfamily.istory.user.controller;

import com.solfamily.istory.user.model.LoginRequest;
import com.solfamily.istory.user.model.UserEntity;
import com.solfamily.istory.user.service.UserInviteService;
import com.solfamily.istory.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;
    private final UserInviteService userInviteService;

    // 초대코드없이 회원가입
    @PostMapping("/join")
    public ResponseEntity userJoin(
            @RequestBody
            UserEntity userEntity
    ) {
        return userService.userJoin(userEntity);
    }

    // 초대코드로 회원가입
    @PostMapping("/joinByInvite")
    public ResponseEntity userJoinByInvite(
            @RequestBody
            UserEntity userEntity
    ) {
        return userInviteService.userJoinByInvite(userEntity);
    }

    // 단일 유저 조회
    @GetMapping("/userId/{userId}")
    public ResponseEntity getUser(
            @PathVariable
            String userId
    ) {
        return userService.getUser(userId);
    }

    // 모든 유저 조회
    @GetMapping ("/all")
    public ResponseEntity getAllUser() {
        return userService.getAlluser();
    }

    // 아이디 중복 확인
    @PostMapping("/checkId")
    public ResponseEntity<Boolean> checkId(
            @RequestParam
            String userId
    ) {
        return userService.checkId(userId);
    }

    // 초대코드 발급
    @PostMapping("/invite")
    public ResponseEntity<String> userInvite(
            @RequestParam(name = "family_key")
            String familyKey
    ) {
        return userInviteService.userInvite(familyKey);
    }

    // 초대코드 유효성 검사
    @PostMapping("/checkInviteCode")
    public ResponseEntity checkInviteCode(
            @RequestParam(name = "invite_code")
            String inviteCode
    ) {
        return userInviteService.checkInviteCode(inviteCode);
    }

    // 유저 로그인
    @PostMapping("/login")
    public ResponseEntity userLogin(
        @RequestBody
        LoginRequest loginRequest
        // HttpSession httpsession
    ) {
        return userService.userLogin(loginRequest);
    }

}
