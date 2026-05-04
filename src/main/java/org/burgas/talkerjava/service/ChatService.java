package org.burgas.talkerjava.service;

import org.burgas.talkerjava.cache.CacheUtil;
import org.burgas.talkerjava.cache.RedisCacheHandler;
import org.burgas.talkerjava.dao.chat.Chat;
import org.burgas.talkerjava.dao.identity.Identity;
import org.burgas.talkerjava.dao.message.Message;
import org.burgas.talkerjava.dto.chat.ChatFullResponse;
import org.burgas.talkerjava.dto.chat.ChatRequest;
import org.burgas.talkerjava.dto.chat.ChatShortResponse;
import org.burgas.talkerjava.dto.group.GroupRequest;
import org.burgas.talkerjava.dto.identity.IdentityFullResponse;
import org.burgas.talkerjava.dto.message.MessageFullResponse;
import org.burgas.talkerjava.mapper.ChatMapper;
import org.burgas.talkerjava.repository.IdentityRepository;
import org.burgas.talkerjava.service.dao.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
public class ChatService implements ListDao<ChatShortResponse>, ReadDao<UUID, Chat, ChatFullResponse>,
        DesignDao<UUID, ChatRequest, ChatFullResponse>, ModifyDao<ChatRequest, ChatFullResponse>,
        GroupHandler<GroupRequest>, RedisCacheHandler<Chat> {

    private final ChatMapper chatMapper;
    private final IdentityRepository identityRepository;

    @Qualifier(value = "chatRedisTemplate")
    private final RedisTemplate<String, ChatFullResponse> chatRedisTemplate;

    @Qualifier(value = "identityRedisTemplate")
    private final RedisTemplate<String, IdentityFullResponse> identityRedisTemplate;

    @Qualifier(value = "messageRedisTemplate")
    private final RedisTemplate<String, MessageFullResponse> messageRedisTemplate;

    public ChatService(ChatMapper chatMapper, IdentityRepository identityRepository,
                       RedisTemplate<String, ChatFullResponse> chatRedisTemplate,
                       RedisTemplate<String, IdentityFullResponse> identityRedisTemplate,
                       RedisTemplate<String, MessageFullResponse> messageRedisTemplate) {
        this.chatMapper = chatMapper;
        this.identityRepository = identityRepository;
        this.chatRedisTemplate = chatRedisTemplate;
        this.identityRedisTemplate = identityRedisTemplate;
        this.messageRedisTemplate = messageRedisTemplate;
    }

    @Override
    public void handleCache(Chat entity) {
        String chatKey = String.format(CacheUtil.CHAT_KEY, entity.getId());
        if (chatRedisTemplate.hasKey(chatKey)) chatRedisTemplate.delete(chatKey);

        Identity admin = entity.getAdmin();
        if (admin != null) {
            String adminKey = String.format(CacheUtil.IDENTITY_KEY, admin.getId());
            if (identityRedisTemplate.hasKey(adminKey)) identityRedisTemplate.delete(adminKey);
        }

        List<Identity> identities = entity.getIdentities();
        if (identities != null && !identities.isEmpty()) {
            identities.forEach(identity -> {
                String identityKey = String.format(CacheUtil.IDENTITY_KEY, identity.getId());
                if (identityRedisTemplate.hasKey(identityKey)) identityRedisTemplate.delete(identityKey);
            });
        }

        List<Message> messages = entity.getMessages();
        if (messages != null && !messages.isEmpty()) {
            messages.forEach(message -> {
                String messageKey = String.format(CacheUtil.MESSAGE_KEY, message.getId());
                if (messageRedisTemplate.hasKey(messageKey)) messageRedisTemplate.delete(messageKey);
            });
        }
    }

    @Override
    public Chat findEntity(UUID uuid) {
        return chatMapper.chatRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));
    }

    @Override
    public List<ChatShortResponse> findAll() {
        return chatMapper.chatRepository.findAll()
                .parallelStream()
                .map(chatMapper::toShortResponse)
                .toList();
    }

    @Override
    public ChatFullResponse findById(UUID uuid) {
        String chatKey = String.format(CacheUtil.CHAT_KEY, uuid);
        if (chatRedisTemplate.hasKey(chatKey)) {
            return chatRedisTemplate.opsForValue().get(chatKey);
        } else {
            ChatFullResponse chatFullResponse = chatMapper.toFullResponse(findEntity(uuid));
            chatRedisTemplate.opsForValue().set(chatKey, chatFullResponse);
            return chatFullResponse;
        }
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public ChatFullResponse create(ChatRequest request) {
        Chat chat = chatMapper.toEntity(request);
        handleCache(chat);
        ChatFullResponse chatFullResponse = chatMapper.toFullResponse(chat);
        String chatKey = String.format(CacheUtil.CHAT_KEY, chatFullResponse.getId());
        chatRedisTemplate.opsForValue().set(chatKey, chatFullResponse);
        return chatFullResponse;
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public ChatFullResponse update(ChatRequest request) {
        if (request.getId() == null) throw new IllegalArgumentException("Chat Request id is null");
        return create(request);
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void delete(UUID uuid) {
        Chat chat = findEntity(uuid);
        chatMapper.chatRepository.delete(chat);
        handleCache(chat);
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void join(GroupRequest request) {
        Identity applicant = identityRepository.findById(Objects.requireNonNull(request.getApplicantId())).orElseThrow();
        Chat chat = findEntity(Objects.requireNonNull(request.getGroupId()));
        List<UUID> chatIds = applicant.getChats().parallelStream().map(Chat::getId).toList();
        if (!chatIds.contains(chat.getId())) {
            applicant.addChat(chat);
            handleCache(chat);
        } else {
            throw new IllegalArgumentException("Applicant is already chat");
        }
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void out(GroupRequest request) {
        Identity applicant = identityRepository.findById(Objects.requireNonNull(request.getApplicantId())).orElseThrow();
        Chat chat = findEntity(Objects.requireNonNull(request.getGroupId()));
        List<UUID> chatIds = applicant.getChats().parallelStream().map(Chat::getId).toList();
        if (chatIds.contains(chat.getId())) {
            applicant.removeChat(chat);
            handleCache(chat);
        } else {
            throw new IllegalArgumentException("Applicant not in chat fro deleting");
        }
    }
}
