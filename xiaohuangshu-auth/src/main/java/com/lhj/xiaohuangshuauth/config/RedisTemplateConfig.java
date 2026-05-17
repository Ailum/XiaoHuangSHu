package com.lhj.xiaohuangshuauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisTemplateConfig {
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        // 璁剧疆 RedisTemplate 鐨勮繛鎺ュ伐鍘?
        redisTemplate.setConnectionFactory(connectionFactory);

        // 浣跨敤 StringRedisSerializer 鏉ュ簭鍒楀寲鍜屽弽搴忓垪鍖?redis 鐨?key 鍊硷紝纭繚 key 鏄彲璇荤殑瀛楃涓?
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // 浣跨敤 Jackson2JsonRedisSerializer 鏉ュ簭鍒楀寲鍜屽弽搴忓垪鍖?redis 鐨?value 鍊? 纭繚瀛樺偍鐨勬槸 JSON 鏍煎紡
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
