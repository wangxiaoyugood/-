package com.wang.seckill.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

   @Bean
   public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory rcf){
       RedisTemplate<String, Object> template = new RedisTemplate<>();
       //key序列化
       template.setKeySerializer(new StringRedisSerializer());
       //value序列化
       template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
       //hash key序列化
       template.setHashKeySerializer(new StringRedisSerializer());
       //hash value序列化
       template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
       //注入连接工厂
       template.setConnectionFactory(rcf);
       return template;
   }


}
