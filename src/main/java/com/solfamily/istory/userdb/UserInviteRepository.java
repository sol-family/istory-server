package com.solfamily.istory.userdb;

import com.solfamily.istory.usermodel.UserInviteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserInviteRepository extends JpaRepository<UserInviteEntity, String> {

    // 유효한 초대코드인지 확인
    @Query(value = "SELECT invite_code, family_key, created_date " +
            "FROM istroy_user_invite " +
            "WHERE is_used = false " +
            "AND expiry_date > NOW() " +
            "AND invite_code = :inviteCode",
            nativeQuery = true)
    Optional<UserInviteEntity> findValidInvite(
           @Param("inviteCode")
            String inviteCode
    );
    
    // 생성된지 30분이 지난 초대코드는 사용된 초대코드로 변경
    @Modifying
    @Query(value = "UPDATE istory_user_invite u " +
            "SET u.is_used = true " +
            "WHERE u.is_used = false " +
            "AND TIMESTAMPDIFF(MINUTE, u.created_date, CURRENT_TIMESTAMP) > 30",
            nativeQuery = true)
    void updateExpiredInvites();

    // 사용된 초대코드는 30분 주기로 자동 삭제
    @Modifying
    @Query(value = "DELETE FROM istory_user_invite WHERE is_used = true", nativeQuery = true)
    void deleteUsedInvites();

}
