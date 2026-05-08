package org.burgas.talkerjava.service;

import jakarta.servlet.http.Part;
import org.burgas.talkerjava.cache.CacheUtil;
import org.burgas.talkerjava.cache.RedisCacheHandler;
import org.burgas.talkerjava.dao.chat.Chat;
import org.burgas.talkerjava.dao.identity.Identity;
import org.burgas.talkerjava.dao.message.Message;
import org.burgas.talkerjava.dao.message.MessageFile;
import org.burgas.talkerjava.dto.chat.ChatFullResponse;
import org.burgas.talkerjava.dto.identity.IdentityFullResponse;
import org.burgas.talkerjava.dto.message.MessageFullResponse;
import org.burgas.talkerjava.dto.message.MessageRequest;
import org.burgas.talkerjava.mapper.MessageFileMapper;
import org.burgas.talkerjava.mapper.MessageMapper;
import org.burgas.talkerjava.service.dao.DesignPartDao;
import org.burgas.talkerjava.service.dao.ReadDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
public class MessageService implements ReadDao<UUID, Message, MessageFullResponse>,
        DesignPartDao<UUID, MessageRequest, MessageFullResponse>, RedisCacheHandler<Message> {

    private final MessageMapper messageMapper;
    private final MessageFileMapper messageFileMapper;

    @Qualifier(value = "messageRedisTemplate")
    private final RedisTemplate<String, MessageFullResponse> messageRedisTemplate;

    @Qualifier(value = "chatRedisTemplate")
    private final RedisTemplate<String, ChatFullResponse> chatRedisTemplate;

    @Qualifier(value = "identityRedisTemplate")
    private final RedisTemplate<String, IdentityFullResponse> identityRedisTemplate;

    public MessageService(
            MessageMapper messageMapper, MessageFileMapper messageFileMapper,
            RedisTemplate<String, MessageFullResponse> messageRedisTemplate,
            RedisTemplate<String, ChatFullResponse> chatRedisTemplate,
            RedisTemplate<String, IdentityFullResponse> identityRedisTemplate
    ) {
        this.messageMapper = messageMapper;
        this.messageFileMapper = messageFileMapper;
        this.messageRedisTemplate = messageRedisTemplate;
        this.chatRedisTemplate = chatRedisTemplate;
        this.identityRedisTemplate = identityRedisTemplate;
    }

    @Override
    public void handleCache(Message entity) {
        String messageKey = String.format(CacheUtil.MESSAGE_KEY, entity.getId());
        if (messageRedisTemplate.hasKey(messageKey)) messageRedisTemplate.delete(messageKey);

        Chat chat = entity.getChat();
        if (chat != null) {
            String chatKey = String.format(CacheUtil.CHAT_KEY, chat.getId());
            if (chatRedisTemplate.hasKey(chatKey)) chatRedisTemplate.delete(chatKey);
        }

        Identity sender = entity.getSender();
        if (sender != null) {
            String senderKey = String.format(CacheUtil.IDENTITY_KEY, sender.getId());
            if (identityRedisTemplate.hasKey(senderKey)) identityRedisTemplate.delete(senderKey);
        }
    }

    @Override
    public Message findEntity(UUID uuid) {
        return messageMapper.messageRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));
    }

    @Override
    public MessageFullResponse findById(UUID uuid) {
        String messageKey = String.format(CacheUtil.MESSAGE_KEY, uuid);
        if (messageRedisTemplate.hasKey(messageKey)) {
            return messageRedisTemplate.opsForValue().get(messageKey);
        } else {
            MessageFullResponse messageFullResponse = messageMapper.toFullResponse(findEntity(uuid));
            messageRedisTemplate.opsForValue().set(messageKey, messageFullResponse);
            return messageFullResponse;
        }
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Throwable.class, Exception.class, RuntimeException.class}
    )
    public MessageFullResponse create(MessageRequest request, List<Part> files) {
        Message entity = messageMapper.toEntity(request);
        Message message = messageMapper.messageRepository.save(entity);
        handleCache(message);
        files.forEach(
                part -> {
                    MessageFile upload = messageFileMapper.upload(message, part);
                    message.getFiles().add(upload);
                }
        );
        return findById(message.getId());
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Throwable.class, Exception.class, RuntimeException.class}
    )
    public void delete(UUID uuid) {
        Message message = findEntity(uuid);
        messageMapper.messageRepository.delete(message);
        handleCache(message);
    }
}
