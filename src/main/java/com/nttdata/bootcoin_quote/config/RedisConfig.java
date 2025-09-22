package com.nttdata.bootcoin_quote.config;

import com.nttdata.model.QuoteRates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfig {

    @Bean
    ReactiveRedisTemplate<String, QuoteRates> quoteRedisTemplate(
            ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<QuoteRates> valueSer =
                new Jackson2JsonRedisSerializer<>(QuoteRates.class);

        RedisSerializationContext<String, QuoteRates> ctx =
                RedisSerializationContext.<String, QuoteRates>newSerializationContext(new StringRedisSerializer())
                        .value(valueSer)
                        .build();

        return new ReactiveRedisTemplate<>(factory, ctx);
    }

    @Bean
    ReactiveStringRedisTemplate reactiveStringRedisTemplate(
            ReactiveRedisConnectionFactory factory) {
        return new ReactiveStringRedisTemplate(factory);
    }
}
