package com.solfamily.istory;

import com.solfamily.istory.Family.model.InvitedUserInfo;
import com.solfamily.istory.global.service.JwtTokenService;
import com.solfamily.istory.global.service.PasswordService;
import com.solfamily.istory.shinhan.service.ShinhanApiService;
import com.solfamily.istory.user.db.UserRepository;
import com.solfamily.istory.user.model.LoginRequest;
import com.solfamily.istory.user.model.UserDto;
import com.solfamily.istory.user.model.UserEntity;
import com.solfamily.istory.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository; // Mock UserRepository

    @Mock
    private PasswordService passwordService; // Mock PasswordService

    @Mock
    private JwtTokenService jwtTokenService; // Mock JwtTokenService

    @Mock
    private HashOperations<String, String, InvitedUserInfo> userInfoHashOperations; // Mock HashOperations

    @Mock
    private HashOperations<String, String, String> invitedUserIdHashOperations; // Mock HashOperations

    @Mock
    private ShinhanApiService shinhanApiService; // Mock ShinhanApiService

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mockito 초기화
    }

    @Test
    void testSignUp_Success() {
        UserDto userDto = new UserDto();
        userDto.setUserId("testUser");
        userDto.setUserPw("testPassword");

        when(userRepository.findById("testUser")).thenReturn(Optional.empty());
        when(passwordService.hashPassword("testPassword")).thenReturn("hashedPassword");

        UserEntity userEntity = new UserEntity();
        userEntity.setUserId("testUser");
        userEntity.setUserPw("hashedPassword");

        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        when(invitedUserIdHashOperations.get("testUser", "inviteCode")).thenReturn(null); // 초대 코드가 없음을 Mocking

        Map<String, Object> shinhanResponse = new HashMap<>();
        shinhanResponse.put("result", true);
        shinhanResponse.put("userKey", "userKey123");

        // 만약 signUp이 Map<String, Object>를 반환하는 경우
        when(shinhanApiService.signUp("testUser")).thenReturn(shinhanResponse); // ResponseEntity를 제거하고 Map으로 반환

        ResponseEntity<Map<String, Object>> response = userService.signUp(userDto);

        // Debugging: 응답 로그 출력
        System.out.println("Response: " + response.getBody());

        assertNotNull(response.getBody(), "Response body should not be null");
        assertTrue((Boolean) response.getBody().get("result"));
        assertEquals("userKey123", userEntity.getUserKey()); // userKey가 올바르게 저장되었는지 검증
    }




    @Test
    void testUserLogin_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserId("testUser");
        loginRequest.setUserPw("testPassword");

        UserEntity userEntity = new UserEntity();
        userEntity.setUserId("testUser");
        userEntity.setUserPw("hashedPassword");

        when(userRepository.findById("testUser")).thenReturn(Optional.of(userEntity));
        when(passwordService.checkPassword("testPassword", "hashedPassword")).thenReturn(true);

        when(jwtTokenService.create(any(), any())).thenReturn("testJwtToken");

        ResponseEntity<Map<String, Object>> response = userService.userLogin(loginRequest);

        assertTrue((Boolean) response.getBody().get("result"));
        assertEquals("testJwtToken", response.getBody().get("jwtToken"));
    }

    @Test
    void testGetUserStatus_UserNotFound() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer someToken");

        // UserService 인스턴스 생성
        UserService userService = new UserService();

        // 테스트 실행
        String userStatus = String.valueOf(userService.getUserStatus(request));

        assertEquals("Expected Status", userStatus);
    }

}
