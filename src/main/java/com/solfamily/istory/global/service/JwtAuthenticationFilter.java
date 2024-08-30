package com.solfamily.istory.global.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements Filter {
    @Value("${JWT_SECRET_KEY}")
    private String secretKey; // 설정한 비밀키 입력

    // 예외 처리할 URL 리스트
    private final Set<String> excludedUrls;
    private final JwtTokenService jwtTokenService;
    private final ObjectMapper objectMapper;

    public void init(FilterConfig filterConfig) throws ServletException {

        // 예외 URL 추가
        excludedUrls.add("/api/v1/user/sign-up");
        excludedUrls.add("/api/v1/user/login");
        excludedUrls.add("/api/v1/dummy/shinhantest");
        excludedUrls.add("/api/v1/dummy/people");
        excludedUrls.add("/api/v1/dummy/familyMission");
        excludedUrls.add("/api/v1/user/all-inquire");
        excludedUrls.add("/api/v1/file/image");
        excludedUrls.add("/api/v1/file/uploadImg");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestUri = httpRequest.getRequestURI();

        // 예외 URL에 해당하는 경우, 검증을 건너뛰고 다음 필터로 전달
        if (excludedUrls.contains(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = httpRequest.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = authorizationHeader.substring(7); // "Bearer "로 시작
        try {
            jwtTokenService.validation(token);
            filterChain.doFilter(request, response); // 다음 필터 또는 컨트롤러로 요청 전달
        } catch (Exception e) {
            String errorCode;

            if (e instanceof SignatureException) {
                errorCode = "J0"; // 토큰 서명 조작 에러
            } else if (e instanceof ExpiredJwtException) {
                errorCode = "J1"; // 토큰 유효기간 만료 에러
            } else {
                errorCode = "J2"; // 토큰 검증 관련 알 수 없는 에러
            }

            sendErrorResponse(httpResponse, errorCode);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String errorCode) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();

        errorResponse.put("result", false);
        errorResponse.put("errorCode", errorCode);

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);
    }
}

