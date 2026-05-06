package org.burgas.talkerjava.service;

import org.burgas.talkerjava.cache.CacheUtil;
import org.burgas.talkerjava.cache.RedisCacheHandler;
import org.burgas.talkerjava.dao.chat.Chat;
import org.burgas.talkerjava.dao.community.Community;
import org.burgas.talkerjava.dao.identity.Identity;
import org.burgas.talkerjava.dto.chat.ChatFullResponse;
import org.burgas.talkerjava.dto.community.CommunityFullResponse;
import org.burgas.talkerjava.dto.identity.IdentityFullResponse;
import org.burgas.talkerjava.dto.identity.IdentityRequest;
import org.burgas.talkerjava.dto.identity.IdentityShortResponse;
import org.burgas.talkerjava.mapper.IdentityMapper;
import org.burgas.talkerjava.service.dao.DesignDao;
import org.burgas.talkerjava.service.dao.ListDao;
import org.burgas.talkerjava.service.dao.ModifyDao;
import org.burgas.talkerjava.service.dao.ReadDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
public class IdentityService implements ListDao<IdentityShortResponse>, ReadDao<UUID, Identity, IdentityFullResponse>,
        DesignDao<UUID, IdentityRequest, IdentityFullResponse>, ModifyDao<IdentityRequest, IdentityFullResponse>,
        RedisCacheHandler<Identity> {

    private final IdentityMapper identityMapper;

    @Qualifier(value = "identityRedisTemplate")
    private final RedisTemplate<String, IdentityFullResponse> identityRedisTemplate;

    @Qualifier(value = "chatRedisTemplate")
    private final RedisTemplate<String, ChatFullResponse> chatRedisTemplate;

    @Qualifier(value = "communityRedisTemplate")
    private final RedisTemplate<String, CommunityFullResponse> communityRedisTemplate;

    public IdentityService(IdentityMapper identityMapper, RedisTemplate<String, IdentityFullResponse> identityRedisTemplate,
                           RedisTemplate<String, ChatFullResponse> chatRedisTemplate,
                           RedisTemplate<String, CommunityFullResponse> communityRedisTemplate) {
        this.identityMapper = identityMapper;
        this.identityRedisTemplate = identityRedisTemplate;
        this.chatRedisTemplate = chatRedisTemplate;
        this.communityRedisTemplate = communityRedisTemplate;
    }

    @Override
    public void handleCache(Identity entity) {
        String identityKey = String.format(CacheUtil.IDENTITY_KEY, entity.getId());
        if (identityRedisTemplate.hasKey(identityKey)) identityRedisTemplate.delete(identityKey);

        Set<Chat> chats = entity.getChats();
        if (!chats.isEmpty()) {
            chats.forEach(chat -> {
                String chatKey = String.format(CacheUtil.CHAT_KEY, chat.getId());
                if (chatRedisTemplate.hasKey(chatKey)) chatRedisTemplate.delete(chatKey);
            });
        }

        Set<Community> communities = entity.getCommunities();
        if (!communities.isEmpty()) {
            communities.forEach(community -> {
                String communityKey = String.format(CacheUtil.COMMUNITY_KEY, community.getId());
                if (communityRedisTemplate.hasKey(communityKey)) communityRedisTemplate.delete(communityKey);
            });
        }
    }

    @Override
    public List<IdentityShortResponse> findAll() {
        return identityMapper.identityRepository.findAll()
                .parallelStream()
                .map(identityMapper::toShortResponse)
                .toList();
    }

    @Override
    public Identity findEntity(UUID uuid) {
        return this.identityMapper.identityRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException(("Identity not found")));
    }

    @Override
    public IdentityFullResponse findById(UUID uuid) {
        String identityKey = String.format(CacheUtil.IDENTITY_KEY, uuid);
        if (identityRedisTemplate.hasKey(identityKey)) {
            return identityRedisTemplate.opsForValue().get(identityKey);
        } else {
            IdentityFullResponse identityFullResponse = identityMapper.toFullResponse(findEntity(uuid));
            identityRedisTemplate.opsForValue().set(identityKey, identityFullResponse);
            return identityFullResponse;
        }
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public IdentityFullResponse create(IdentityRequest request) {
        Identity entity = identityMapper.toEntity(request);
        Identity identity = identityMapper.identityRepository.save(entity);
        IdentityFullResponse identityFullResponse = identityMapper.toFullResponse(identity);
        String identityKey = String.format(CacheUtil.IDENTITY_KEY, identityFullResponse.getId());
        identityRedisTemplate.opsForValue().set(identityKey, identityFullResponse);
        return identityFullResponse;
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public IdentityFullResponse update(IdentityRequest request) {
        if (request.getId() == null) throw new IllegalArgumentException("Identity request id is null");
        Identity entity = identityMapper.toEntity(request);
        Identity identity = identityMapper.identityRepository.save(entity);
        handleCache(identity);
        IdentityFullResponse identityFullResponse = identityMapper.toFullResponse(identity);
        String identityKey = String.format(CacheUtil.IDENTITY_KEY, identityFullResponse.getId());
        identityRedisTemplate.opsForValue().set(identityKey, identityFullResponse);
        return identityFullResponse;
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void delete(UUID uuid) {
        Identity identity = findEntity(uuid);
        handleCache(identity);
        identityMapper.identityRepository.delete(identity);
    }
}
