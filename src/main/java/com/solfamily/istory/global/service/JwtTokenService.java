package com.solfamily.istory.global.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JwtTokenService {
    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

    // 토큰 발행
    public String create(
            Map<String, Object> claims,
            LocalDateTime expiredAt
    ) {

        // 비밀키 생성
        var key = Keys.hmacShaKeyFor(secretKey.getBytes());
        var _expiredAt = Date.from(expiredAt.atZone(ZoneId.systemDefault()).toInstant()); // LocalDateTime을 Date 형식으로 변환

        // JWT Token 생성 (미리 만들어진 라이브러리를 사용한)
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, key) // H256해시 알고리즘과 비밀키를 사용하여 서명 생성
                .setClaims(claims)
                .setExpiration(_expiredAt)
                .compact();
    }

    // 토큰 검증
    public void validation(
            String token
    ) throws Exception {
        Map<String, Object> response = new HashMap<>();

        var key = Keys.hmacShaKeyFor(secretKey.getBytes());

        // parser 생성
        var parser = Jwts.parser().setSigningKey(key); // parser에 암호화 할 때 사용했던 비밀키 등록

        Jws<Claims> result = parser.parseClaimsJws(token); // 토큰을 검증하고 결과를 반환, 올바르지 않은 토큰이면 에러발생

        result.getBody().entrySet().forEach(value -> {
            log.info("key : {}, value : {}", value.getKey(), value.getValue());
        });
    }

    // claims에 담겨있는 userId 복호화
    public String getUserIdByClaims(
            String jwtToken
    ) {
        // 클레임스 복호화
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey.getBytes()) // 서명 검증을 위한 비밀키 설정
                .parseClaimsJws(jwtToken)
                .getBody(); // claims 부분만 추출

        // 원하는 클레임 정보 추출
        String userId = claims.get("userId", String.class);

        return userId;
    }
}
