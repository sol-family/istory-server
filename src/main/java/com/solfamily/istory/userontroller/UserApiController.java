package com.solfamily.istory.userontroller;

import com.solfamily.istory.usermodel.UserDto;
import com.solfamily.istory.usermodel.UserEntity;
import com.solfamily.istory.userservice.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserServiceImpl userService;

    @PostMapping("user/join")
    public UserDto userJoin(
            @RequestBody
            UserEntity userEntity
    ) {
        return userService.userJoin(userEntity);
    }

    @GetMapping ("user/inquiry")
    public List<UserDto> userInquiry() {
        return userService.userInquiry();
    }

    @PostMapping("user/checkDuplicate")
    public boolean checkDuplicate(
            @RequestParam
            String userId
    ) {
        return userService.checkDuplicate(userId);
    }

    @GetMapping("user/invite")
    public String userInvite(
            String familyKey,
            HttpServletRequest request
    ) {
        userService.userInvite(familyKey, request);

    }


}
