package com.solfamily.istory.userservice;

import com.solfamily.istory.userdb.UserRepository;
import com.solfamily.istory.usermodel.UserDto;
import com.solfamily.istory.usermodel.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    public UserDto userJoin(UserEntity userEntity) {
        var entity = userRepository.save(userEntity);

        return userConverter.toDto(userEntity);
    }

    public List<UserDto> userInquiry() {
        var userEntities = userRepository.findAll();

        return userEntities.stream().map(userConverter::toDto).toList();
    }

    public boolean checkDuplicate(String userId) {
        var entity = userRepository.findById(userId);

        // id가 존재하면 true, 존재하지 않으면 false
        return entity.isPresent();
    }

    public void userInvite(String familyKey, HttpServletRequest request) {

        // 세션 객체 생성
        HttpSession session =request.getSession();

        // 랜덤한 초대코드 생성
        String inviteCode = UUID.randomUUID().toString();

        // 세션 속성에 familyKey를 key, inviteCode를 value로 저장
        session.setAttribute("familKey", inviteCode);

        // 세션 유효기간을 10분(600초)으로 설정
        session.setMaxInactiveInterval(600); // 초 단위
    }
}
