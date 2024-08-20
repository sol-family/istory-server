package com.solfamily.istory.db.user;

import java.time.LocalDateTime;
import java.util.Optional;

import com.solfamily.istory.model.user.UserInviteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface UserInviteRepository extends JpaRepository<UserInviteEntity, String> {

    // 유효한 초대코드인지 확인
    Optional<UserInviteEntity> findByInviteCodeAndIsUsedFalseAndExpiryDateAfter(
            String inviteCode,
            LocalDateTime now
    );

    // 생성된지 30분이 지난 초대코드를 사용된 초대코드로 변경
    @Modifying
    void setIsUsedTrueForInvitesExpiryDateBefore(
            LocalDateTime now
    );

    // 사용된 초대코드를 30분 주기로 자동 삭제
    @Modifying
    void deleteByIsUsedTrue();
}
