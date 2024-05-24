package com.ssafy.algonote.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.algonote.notification.dto.response.NotificationResDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    /**  RedisConnectionFactory를 통해 내장 혹은 외부의 Redis를 연결한다.
     *   Lettuce를 사용했다. 가장 많이 사용되는 라이브러로 Spring Data Redis에
     *   내장되어있다.
     */

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    /**
     * RedisOperations은 RedisTemplate의 인터페이스이다. 인터페이스로 정의한 이유는
     * 테스트 코드에서 테스트하기 편하게 하기 위해서이다.
     * redisOperations를 통해 RedisConnection에서 넘겨준 byte 값을 객체 직렬화한다.
     * Redis와 통신할때 사용
     */
    @Bean
    public RedisOperations<String, NotificationResDto> eventRedisOperations(
        RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
        final Jackson2JsonRedisSerializer<NotificationResDto> jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
            NotificationResDto.class);
        jsonRedisSerializer.setObjectMapper(objectMapper);
        final RedisTemplate<String, NotificationResDto> eventRedisTemplate = new RedisTemplate<>();
        eventRedisTemplate.setConnectionFactory(redisConnectionFactory);
        eventRedisTemplate.setKeySerializer(RedisSerializer.string());
        eventRedisTemplate.setValueSerializer(jsonRedisSerializer);
        eventRedisTemplate.setHashKeySerializer(RedisSerializer.string());
        eventRedisTemplate.setHashValueSerializer(jsonRedisSerializer);
        return eventRedisTemplate;
    }

    /**
     * RedisMessageListenerContainer는 Spring Data Redis에서 제공하는 클래스이다.
     * 컨테이너는 메시지가 도착하면 등록된 MessageListener를 호출하여 메시지를 처리한다.
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        final RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        return redisMessageListenerContainer;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
