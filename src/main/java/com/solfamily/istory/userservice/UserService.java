package com.solfamily.istory.userservice;

import com.solfamily.istory.userdb.UserInviteRepository;
import com.solfamily.istory.userdb.UserRepository;
import com.solfamily.istory.usermodel.UserDto;
import com.solfamily.istory.usermodel.UserEntity;
import com.solfamily.istory.usermodel.UserInviteEntity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    // 초대코드 없이 회원가입
    public UserDto userJoin(UserEntity userEntity) {
        // 고유한 패밀리키(식별키) 생성
        String familyKey = "familyKey/" + UUID.randomUUID().toString();
        userEntity.setFamilyKey(familyKey);

        var entity = userRepository.save(userEntity);

        return userConverter.toDto(userEntity);
    }

    // 유저 한 명 유저 아이디로 조회
    public UserDto getUser(String userId) {
        var optionalUserEntity = userRepository.findById(userId);

        if(optionalUserEntity.isEmpty()) {
            throw new RuntimeException("userId Not Found");
        }

        return userConverter.toDto(optionalUserEntity.get());
    }

    // 모든 유저 조회
    public List<UserDto> getAlluser() {
        var userEntities = userRepository.findAll();

        return userEntities.stream().map(userConverter::toDto).toList();
    }

    // 유저 아이디 중북체크
    public boolean checkId(String userId) {
        var entity = userRepository.findById(userId);

        // id가 존재하면 true, 존재하지 않으면 false
        return entity.isPresent();
    }
}
