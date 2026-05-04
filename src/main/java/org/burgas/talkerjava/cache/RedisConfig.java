package org.burgas.talkerjava.cache;

import org.burgas.talkerjava.dto.chat.ChatFullResponse;
import org.burgas.talkerjava.dto.comment.CommentFullResponse;
import org.burgas.talkerjava.dto.community.CommunityFullResponse;
import org.burgas.talkerjava.dto.identity.IdentityFullResponse;
import org.burgas.talkerjava.dto.message.MessageFullResponse;
import org.burgas.talkerjava.dto.publication.PublicationFullResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, IdentityFullResponse> identityRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, IdentityFullResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JacksonJsonRedisSerializer<>(IdentityFullResponse.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, ChatFullResponse> chatRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ChatFullResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JacksonJsonRedisSerializer<>(ChatFullResponse.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, MessageFullResponse> messageRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, MessageFullResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JacksonJsonRedisSerializer<>(MessageFullResponse.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, CommunityFullResponse> communityRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, CommunityFullResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JacksonJsonRedisSerializer<>(CommunityFullResponse.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, PublicationFullResponse> publicationRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, PublicationFullResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JacksonJsonRedisSerializer<>(PublicationFullResponse.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, CommentFullResponse> commentRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, CommentFullResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JacksonJsonRedisSerializer<>(CommentFullResponse.class));
        return template;
    }
}
