package com.solfamily.istory.global.configure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonParser {

    public String toJson(String msg) {
        ObjectMapper om = new ObjectMapper();
        try {
            // msg 문자열을 JSON 형식의 문자열로 변환
            return om.writeValueAsString(msg);
        } catch (Exception e) {
            return null;
        }
    }
}
