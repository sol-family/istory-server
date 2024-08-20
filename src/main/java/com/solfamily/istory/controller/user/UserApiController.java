package com.solfamily.istory.controller.user;

import com.solfamily.istory.model.user.LoginRequest;
import com.solfamily.istory.model.user.UserDto;
import com.solfamily.istory.model.user.UserEntity;
import com.solfamily.istory.service.user.UserInviteService;
import com.solfamily.istory.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;
    private final UserInviteService userInviteService;

    // 초대코드없이 회원가입
    @PostMapping("user/join")
    public UserDto userJoin(
            @RequestBody
            UserEntity userEntity
    ) {
        return userService.userJoin(userEntity);
    }

    // 초대코드로 회원가입
    @PostMapping("user/joinByInvite")
    public UserDto userJoinByInvite(
            @RequestBody
            UserEntity userEntity,
            HttpServletRequest request
    ) {
        return userInviteService.userJoinByInvite(userEntity, request);
    }

    // 모든 유저 조회
    @GetMapping ("user/all")
    public List<UserDto> getAllUser() {
        return userService.getAlluser();
    }

    // 단일 유저 조회
    @GetMapping("user/userId/{userId}")
    public UserDto getUser(
            @PathVariable
            String userId
    ) {
        return userService.getUser(userId);
    }

    // 아이디 중복 확인
    @PostMapping("user/checkId")
    public boolean checkId(
            @RequestParam
            String userId
    ) {
        return userService.checkId(userId);
    }

    // 초대코드 발급
    @PostMapping("user/invite")
    public String userInvite(
            @RequestParam(name = "family_key")
            String familyKey
    ) {
        return userInviteService.userInvite(familyKey);
    }

    // 유저 로그인
    @PostMapping("/user/login")
    public ResponseEntity<> userLogin(
        @RequestBody
        LoginRequest loginRequest,
        HttpSession httpsession
    ) {
        return userService.userLogin(loginRequest, httpsession);
    }

}
