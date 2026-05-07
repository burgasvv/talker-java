package org.burgas.talkerjava.service;

import org.burgas.talkerjava.cache.CacheUtil;
import org.burgas.talkerjava.cache.RedisCacheHandler;
import org.burgas.talkerjava.dao.community.Community;
import org.burgas.talkerjava.dao.identity.Identity;
import org.burgas.talkerjava.dao.publication.Publication;
import org.burgas.talkerjava.dto.community.CommunityFullResponse;
import org.burgas.talkerjava.dto.community.CommunityRequest;
import org.burgas.talkerjava.dto.group.GroupRequest;
import org.burgas.talkerjava.dto.identity.IdentityFullResponse;
import org.burgas.talkerjava.dto.publication.PublicationFullResponse;
import org.burgas.talkerjava.mapper.CommunityMapper;
import org.burgas.talkerjava.service.dao.DesignDao;
import org.burgas.talkerjava.service.dao.GroupHandler;
import org.burgas.talkerjava.service.dao.ModifyDao;
import org.burgas.talkerjava.service.dao.ReadDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
public class CommunityService implements RedisCacheHandler<Community>, ReadDao<UUID, Community, CommunityFullResponse>,
        DesignDao<UUID, CommunityRequest, CommunityFullResponse>, ModifyDao<CommunityRequest, CommunityFullResponse>,
        GroupHandler<GroupRequest> {

    private final CommunityMapper communityMapper;
    private final IdentityService identityService;

    @Qualifier(value = "communityRedisTemplate")
    private final RedisTemplate<String, CommunityFullResponse> communityRedisTemplate;

    @Qualifier(value = "identityRedisTemplate")
    private final RedisTemplate<String, IdentityFullResponse> identityRedisTemplate;

    @Qualifier(value = "publicationRedisTemplate")
    private final RedisTemplate<String, PublicationFullResponse> publicationRedisTemplate;

    public CommunityService(
            CommunityMapper communityMapper, IdentityService identityService,
            RedisTemplate<String, CommunityFullResponse> communityRedisTemplate,
            RedisTemplate<String, IdentityFullResponse> identityRedisTemplate,
            RedisTemplate<String, PublicationFullResponse> publicationRedisTemplate
    ) {
        this.communityMapper = communityMapper;
        this.identityService = identityService;
        this.communityRedisTemplate = communityRedisTemplate;
        this.identityRedisTemplate = identityRedisTemplate;
        this.publicationRedisTemplate = publicationRedisTemplate;
    }

    @Override
    public void handleCache(Community entity) {
        String communityKey = String.format(CacheUtil.COMMUNITY_KEY, entity.getId());
        if (communityRedisTemplate.hasKey(communityKey)) communityRedisTemplate.delete(communityKey);

        Identity admin = entity.getAdmin();
        if (admin != null) {
            String adminKey = String.format(CacheUtil.IDENTITY_KEY, admin.getId());
            if (identityRedisTemplate.hasKey(adminKey)) identityRedisTemplate.delete(adminKey);
        }

        Set<Identity> identities = entity.getIdentities();
        if (!identities.isEmpty()) {
            identities.forEach(identity -> {
                String identityKey = String.format(CacheUtil.IDENTITY_KEY, identity.getId());
                if (identityRedisTemplate.hasKey(identityKey)) identityRedisTemplate.delete(identityKey);
            });
        }

        Set<Publication> publications = entity.getPublications();
        if (!publications.isEmpty()) {
            publications.forEach(publication -> {
                String publicationKey = String.format(CacheUtil.PUBLICATION_KEY, publication.getId());
                if (publicationRedisTemplate.hasKey(publicationKey)) publicationRedisTemplate.delete(publicationKey);
            });
        }
    }

    @Override
    public Community findEntity(UUID uuid) {
        return communityMapper.communityRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));
    }

    @Override
    public CommunityFullResponse findById(UUID uuid) {
        String communityKey = String.format(CacheUtil.COMMUNITY_KEY, uuid);
        if (communityRedisTemplate.hasKey(communityKey)) {
            return communityRedisTemplate.opsForValue().get(communityKey);
        } else {
            CommunityFullResponse communityFullResponse = communityMapper.toFullResponse(findEntity(uuid));
            communityRedisTemplate.opsForValue().set(communityKey, communityFullResponse);
            return communityFullResponse;
        }
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Throwable.class, Exception.class, RuntimeException.class}
    )
    public CommunityFullResponse create(CommunityRequest request) {
        Community community = communityMapper.toEntity(request);
        handleCache(community);
        CommunityFullResponse communityFullResponse = communityMapper.toFullResponse(community);
        String communityKey = String.format(CacheUtil.COMMUNITY_KEY, communityFullResponse.getId());
        communityRedisTemplate.opsForValue().set(communityKey, communityFullResponse);
        return communityFullResponse;
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Throwable.class, Exception.class, RuntimeException.class}
    )
    public CommunityFullResponse update(CommunityRequest request) {
        if (request.getId() == null) throw new IllegalArgumentException("Community Request id is null");
        return create(request);
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Throwable.class, Exception.class, RuntimeException.class}
    )
    public void delete(UUID uuid) {
        Community community = findEntity(uuid);
        communityMapper.communityRepository.delete(community);
        handleCache(community);
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Throwable.class, Exception.class, RuntimeException.class}
    )
    public void join(GroupRequest request) {
        Identity identity = identityService.findEntity(Objects.requireNonNull(request.getApplicantId()));
        Community community = findEntity(Objects.requireNonNull(request.getGroupId()));
        Set<UUID> identityIds = community.getIdentities().parallelStream().map(Identity::getId).collect(Collectors.toSet());
        if (!identityIds.contains(identity.getId())) {
            community.addIdentity(identity);
            handleCache(community);
        } else {
            throw new IllegalArgumentException("Identity already in community");
        }
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Throwable.class, Exception.class, RuntimeException.class}
    )
    public void out(GroupRequest request) {
        Identity identity = identityService.findEntity(Objects.requireNonNull(request.getApplicantId()));
        Community community = findEntity(Objects.requireNonNull(request.getGroupId()));
        Set<UUID> identityIds = community.getIdentities().parallelStream().map(Identity::getId).collect(Collectors.toSet());
        if (identityIds.contains(identity.getId())) {
            handleCache(community);
            community.removeIdentity(identity);
        } else {
            throw new IllegalArgumentException("Identity not in community");
        }
    }
}
