package com.solfamily.istory.Family.service;

import com.solfamily.istory.Family.model.FamilyEntity;
import com.solfamily.istory.Family.model.InviteCodeRequest;
import com.solfamily.istory.Family.model.InvitedUserInfo;
import com.solfamily.istory.global.service.JwtTokenService;
import com.solfamily.istory.Family.db.FamilyRepository;
import com.solfamily.istory.user.db.UserRepository;
import com.solfamily.istory.user.model.UserDto;
import com.solfamily.istory.user.model.UserEntity;
import com.solfamily.istory.user.service.UserConverterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FamilyService {

    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final JwtTokenService jwtTokenService;
    private final UserConverterService userConverterService;
    private final HashOperations<String, String, InvitedUserInfo> userInfoHashOperations; // Redis의 HashOperations 빈 주입
    private final HashOperations<String, String, String> invitedUserIdHashOperations; // Redis의 HashOperations 빈 주입

    public ResponseEntity<Map<String, Object>> getInviteCode(
            HttpServletRequest request
    ) {
        Map<String, Object> response = new HashMap<>();

        // 클라이언트로부터 jwtToken을 받아옴
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7); // 토큰 추출

        // 토큰으로부터 userId 추출
        String userId = jwtTokenService.getUserIdByClaims(token);

        // userId의 해시값을 16진수로 변환하여 초대코드 생성 -> 중복 초대코드 발생 예방
        int hash = userId.hashCode();
        String inviteCode = String.valueOf(Integer.toHexString(hash));

        // 랜덤 값 + 초대코드를 합쳐 패밀리키 중복 발생 예방
        String familyKey = UUID.randomUUID() + inviteCode;

        Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);

        // 아이스토리 DB에서 조회된 유저가 없을때
        if (optionalUserEntity.isEmpty()) {
            String errorCode = "U0";
            response.put("result", false);
            response.put("errorCode", errorCode);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        }

        // 초대된 가족구성원 정보 객체로 만들어서 패밀리키, 대표자 아이디, 가족구성원 아이디 저장
        InvitedUserInfo userInfo = new InvitedUserInfo();
        userInfo.setFamilyKey(familyKey);
        userInfo.setRepresentativeId(userId);
        userInfo.getMemberId().add(userId);

        // 만든 가족구성원 정보 객체 redis에 등록
        userInfoHashOperations.put(inviteCode, "userInfo", userInfo);
        invitedUserIdHashOperations.put(userId, "inviteCode", inviteCode);

        // 응답 생성
        response.put("result", true);
        response.put("inviteCode", inviteCode);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    public ResponseEntity<Map<String, Object>> getRepresentativeName(
            InviteCodeRequest inviteCodeRequest
    ) {
        Map<String, Object> response = new HashMap<>();

        // redis에서 가족구성원 정보 객체 받아옴
        InvitedUserInfo userInfo = userInfoHashOperations.get(inviteCodeRequest.getInviteCoode(), "userInfo");

        if (userInfo == null) {
            String errorCode = "R0"; // redis 가족구성원 정보 관련 에러 발생
            response.put("result", false);
            response.put("errorCode", errorCode);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        }

        // 대표자 이름을 아이스토리 db로부터 가져옴
        String representaiveName = userRepository.findUserNameByUserId(userInfo.getRepresentativeId()).getUserName();

        response.put("result", true);
        response.put("representaiveName", representaiveName);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    public ResponseEntity<Map<String, Object>> hasInviteCode(
            String userId
    ) {
        Map<String, Object> response = new HashMap<>();

        String familyKey = userRepository.findFamilyKeyByUserId(userId).getFamilyKey();

        // 패밀리키가 존재하지 않는다면, 아직 가족이 확정되지 않은 상태
        if (familyKey == null) {
            String inviteCode = invitedUserIdHashOperations.get(userId, "inviteCode");

            response.put("hasFamily", false);
            // inviteCode가 없으면 초대코드 페이지로, 있으면 가족구성준비 페이지로
            response.put("inviteCode", inviteCode == null ? "" : inviteCode); // redis에 userId로 접근해서 inviteCode 받아오기
        } else {
            // 패밀리키가 존재한다면 가족구성완료
            response.put("hasFamily", true);
            response.put("inviteCode", "");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    public ResponseEntity<Map<String, Object>> acceptInvite(
            HttpServletRequest request,
            InviteCodeRequest inviteCodeRequest
    ) {
        Map<String, Object> response = new HashMap<>();

        // 클라이언트로부터 jwtToken을 받아옴
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7); // 토큰 추출

        // 토큰으로부터 userId 추출
        String userId = jwtTokenService.getUserIdByClaims(token);

        String inviteCode = inviteCodeRequest.getInviteCoode();

        // redis에 가족구성원 유저 아이디 업데이트
        InvitedUserInfo userInfo = userInfoHashOperations.get(inviteCode, "userInfo");
        userInfo.getMemberId().add(userId);

        // 레디스에 userId를 주식별자로 한 레코드 생성
        invitedUserIdHashOperations.put(userId, "inviteCode", inviteCode);

        response.put("result", true);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    public ResponseEntity<Map<String, Object>> getAllUserInfo(
            InviteCodeRequest inviteCodeRequest
    ) {
        Map<String, Object> response = new HashMap<>();

        String inviteCode = inviteCodeRequest.getInviteCoode();

        // redis에서 가족구성원 정보 객체를 받아옴
        InvitedUserInfo userInfo = userInfoHashOperations.get(inviteCode, "userInfo");

        // 가족구성원 정보가 redis에 존재하지 않으면
        if (userInfo == null) {
            String errorCode = "R0"; // redis 가족구성원 정보 관련 에러 발생
            response.put("result", false);
            response.put("errorCode", errorCode);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        }

        List<String> userIdList = userInfo.getMemberId();
        List<UserDto> userDtoList = new ArrayList<>();

        for (int i = 0; i < userIdList.size(); i++) {
            String userId = userIdList.get(i); // redis에서 가져온 userIdList에서 userId를 하나씩 가져옴
            UserEntity userEntity = userRepository.findById(userId).get(); // 아이스토리 db로부터 해당 userEntity 가져옴
            UserDto userDto = userConverterService.toDto(userEntity); // userEntity를 userDto로 변환
            userDtoList.add(userDto); // userDto 리스트에 userDto 저장
        }

        response.put("result", true);
        response.put("user  List", userDtoList);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    public ResponseEntity<Map<String, Object>> expiredInvitedStatus(
            InviteCodeRequest inviteCodeRequest
    ) {
        Map<String, Object> response = new HashMap<>();
        String inviteCode = inviteCodeRequest.getInviteCoode();

        // redis에서 가족구성원 정보 객체를 받아옴
        InvitedUserInfo userInfo = userInfoHashOperations.get(inviteCode, "userInfo");

        // 가족구성원 정보가 redis에 존재하지 않으면
        if (userInfo == null) {
            String errorCode = "R0"; // redis 가족구성원 정보 관련 에러 발생
            response.put("result", false);
            response.put("errorCode", errorCode);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        }

        userInfoHashOperations.delete(inviteCode);

        List<String> userIdList = userInfo.getMemberId();

        for (int i = 0; i < userIdList.size(); i++) {
            String userId = userIdList.get(i); // redis에서 가져온 userIdList에서 userId를 하나씩 가져옴
            invitedUserIdHashOperations.delete(userId); // redis에 존재하는 userId를 주식별자로 한 레코드를 하나씩 삭제
        }

        response.put("result", true);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    public ResponseEntity<Map<String, Object>> excludeUser(
            HttpServletRequest request,
            InviteCodeRequest inviteCodeRequest
    ) {
        Map<String, Object> response = new HashMap<>();

        // 클라이언트로부터 jwtToken을 받아옴
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7); // 토큰 추출

        // 토큰으로부터 userId 추출
        String userId = jwtTokenService.getUserIdByClaims(token);

        String inviteCode = inviteCodeRequest.getInviteCoode();

        // redis 저장된 userId 주식별자로 가지는 레코드를 삭제
        invitedUserIdHashOperations.delete(userId);

        // redis에 저장된 inviteCode 주식별자로 가지는 레코드의 userId value 삭제 후 업데이트
        InvitedUserInfo userInfo = userInfoHashOperations.get(inviteCode, "userInfo");
        List<String> userIdList = userInfo.getMemberId();
        userIdList.remove(userId);

        // 레디스 업데이트
        userInfoHashOperations.put(inviteCode, "userInfo", userInfo);

        response.put("result", true);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    public ResponseEntity<Map<String, Object>> confirmFamily(
            InviteCodeRequest inviteCodeRequest
    ) {
        Map<String, Object> response = new HashMap<>();

        String inviteCode = inviteCodeRequest.getInviteCoode();

        // redis에서 초대된 가족구성원 정보 객체를 받아옴
        InvitedUserInfo userInfo = userInfoHashOperations.get(inviteCode, "userInfo");

        // redis에서 초대코드를 주식별자로 하는 레코드 삭제 및 userId를 주식별자로 하는 레코드 삭제
        expiredInvitedStatus(inviteCodeRequest);

        // 패밀리테이블에 들어갈 familyEntity 생성 후 패밀리테이블 저장
        FamilyEntity familyEntity = new FamilyEntity();
        familyEntity.setFamilyKey(userInfo.getFamilyKey());
        familyEntity.setRepesentiveUserId(userInfo.getRepresentativeId());
        familyRepository.save(familyEntity);

        // userId가 저장된 userIdList
        List<String> userIdList = userInfo.getMemberId();

        // 유저마다 패밀리키 업데이트 하고 아이스토리 db에 저장
        for (int i = 0; i < userIdList.size(); i++) {
            String userId = userIdList.get(i); // redis에서 가져온 userIdList에서 userId를 하나씩 가져옴
            UserEntity userEntity = userRepository.findById(userId).get(); // 아이스토리 db에서 userEntity 가져옴
            userEntity.setFamilyKey(userInfo.getFamilyKey()); // 패밀리키 업데이트

            userRepository.save(userEntity);
        }

        response.put("result", true);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
