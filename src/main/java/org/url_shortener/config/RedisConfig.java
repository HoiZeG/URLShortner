package org.url_shortener.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.url_shortener.entity.Url;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final ObjectMapper objectMapper;

    @Bean
    public RedisTemplate<String, Url> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Url> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer<Url> serializer = new Jackson2JsonRedisSerializer<>(Url.class);
        serializer.setObjectMapper(objectMapper);
        template.setValueSerializer(serializer);
        return template;
    }
}
