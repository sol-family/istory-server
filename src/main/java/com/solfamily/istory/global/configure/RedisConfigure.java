package com.solfamily.istory.global.configure;

import com.solfamily.istory.Family.model.InvitedUserInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfigure {

    @Bean
    public RedisTemplate<String, InvitedUserInfo> redisTemplateForUserInfo(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, InvitedUserInfo> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplateForUserId(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public HashOperations<String, String, InvitedUserInfo> userInfoHashOperations(@Qualifier("redisTemplateForUserInfo") RedisTemplate<String, InvitedUserInfo> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    @Bean
    public HashOperations<String, String, String> invitedUserIdHashOperations(@Qualifier("redisTemplateForUserId")RedisTemplate<String, String> redisTemplate) {
        return redisTemplate.opsForHash();
    }
}
