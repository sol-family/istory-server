package com.solfamily.istory.service.user;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

    @Slf4j
    @Service
    public class JwtTokenService {

        private static String secretKey = "JWTTokenForShinhanHakertonXSsafy";

        // 토큰 발행
        public static String create(
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
        public static void validation(String token) {
            var key = Keys.hmacShaKeyFor(secretKey.getBytes());

            // parser 생성
            var parser = Jwts.parser()
                    .setSigningKey(key); // parser에 비밀키 등록

            try {
                var result = parser.parseClaimsJws(token);
                result.getBody().entrySet().forEach(value -> {
                    log.info("key : {}, value : {}", value.getKey(), value.getValue());
                });
            } catch (Exception e) {
                if (e instanceof SignatureException) {

                }

                if (e instanceof ExpiredJwtException) {

                } else {

                }
            }
        }
    }
