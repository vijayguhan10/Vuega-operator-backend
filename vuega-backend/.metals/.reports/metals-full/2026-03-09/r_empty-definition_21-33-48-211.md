error id: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/Config/RedisConfig.java:_empty_/RedisTemplate#
file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/Config/RedisConfig.java
empty definition using pc, found symbol in pc: _empty_/RedisTemplate#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 631
uri: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/Config/RedisConfig.java
text:
```scala
package net.vuega.vuega_backend.Operator_pannel.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplat@@e<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/RedisTemplate#