package com.solfamily.istory.userservice;

import com.solfamily.istory.userdb.UserInviteRepository;
import com.solfamily.istory.userdb.UserRepository;
import com.solfamily.istory.usermodel.UserDto;
import com.solfamily.istory.usermodel.UserEntity;
import com.solfamily.istory.usermodel.UserInviteEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class UserInviteService {

    private final UserRepository userRepository;
    private final UserInviteRepository userInviteRepository;
    private final UserConverter userConverter;

    // 초대코드로 회원가입
    public UserDto userJoinByInvite(
            UserEntity userEntity,
            HttpServletRequest request
    ) {
        // 유효한 초대코드인지 확인
        String inviteCode = request.getParameter("inviteCode");

        var OptionalUserInviteEntity = userInviteRepository.findValidInvite(inviteCode);

        // 유효하지 않은 초대코드라면
        if(OptionalUserInviteEntity.isEmpty()) {
            throw new RuntimeException("Not Exist InviteCode");
        }

        String familyKey = OptionalUserInviteEntity.get().getFamilyKey();

        userEntity.setFamilyKey(familyKey);

        var entity = userRepository.save(userEntity);

        return userConverter.toDto(userEntity);
    }

    public String userInvite(String familyKey) {
        // 랜덤한 초대코드 생성
        String inviteCode = UUID.randomUUID().toString();

        // DB에 생성된 inviteCode 관련 정보 저장
        UserInviteEntity userInviteEntity = new UserInviteEntity();
        userInviteEntity.setInviteCode(inviteCode);
        userInviteEntity.setFamilyKey(familyKey);
        userInviteEntity.setExpiryDate(LocalDateTime.now().plusMinutes(30)); // 초대코드 유효시간 30분
        userInviteEntity.setUsed(false);
        userInviteEntity.setCreatedDate(LocalDateTime.now());
        userInviteRepository.save(userInviteEntity);

        // 초대 코드 반환
        return inviteCode;
    }

    // 생성된지 30분이 지난 초대코드는 30분 주기로 사용된 초대코드로 변경
    @Scheduled(fixedRate = 30 * 60 * 1000) // 30분마다 실행
    @Transactional
    public void updateExpiredInvites() {
        userInviteRepository.updateExpiredInvites();
    }

    // 사용된 초대코드는 30분 주기로 삭제
    @Scheduled(fixedRate = 30 * 60 * 1000) // 30분마다 실행
    @Transactional
    public void deleteUsedInvites() {
        userInviteRepository.deleteUsedInvites();
    }
}
