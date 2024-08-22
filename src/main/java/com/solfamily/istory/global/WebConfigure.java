    package com.solfamily.istory.global;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.web.servlet.config.annotation.CorsRegistry;
    import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

    @Configuration
    public class WebConfigure implements WebMvcConfigurer {

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**") // 모든 경로
                    .allowedOrigins("http://localhost:3000") // 클라이언트쪽 서버 주소(테스팅 환경용)
                    .allowedMethods("GET", "POST", "OPTIONS") // 허용하는 메서드
                    .allowedHeaders("*") // 헤더
                    .allowCredentials(true) // 인증정보
                    .maxAge(1800); // 프리플라이트 캐시 30분 설정
        }
    }
