package com.solfamily.istory.user.db;

import java.time.LocalDateTime;
import java.util.Optional;

import com.solfamily.istory.user.model.UserInviteCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserInviteRepository extends JpaRepository<UserInviteCodeEntity, String> {

    // 유효한 초대코드인지 확인
    Optional<UserInviteCodeEntity> findByInviteCodeAndIsUsedFalseAndExpiryDateAfter(
            String inviteCode,
            LocalDateTime now
    );

    // 생성된지 30분이 지난 초대코드를 사용된 초대코드로 변경
    // redis로 변경예정
    @Modifying
    @Query(value = "UPDATE istory_user_invite u " +
            "SET u.is_used = true " +
            "WHERE u.is_used = false " +
            "AND u.created_date < :now - INTERVAL 30 MINUTE",
            nativeQuery = true
    )
    void checkExpiredInvitesAsUsed(@Param("now") LocalDateTime now); // SQL injection 예방을 위한 정적 할당

    // 사용된 초대코드를 30분 주기로 자동 삭제
    // redis로 변경예정
    @Modifying
    void deleteByisUsedTrue();
}
