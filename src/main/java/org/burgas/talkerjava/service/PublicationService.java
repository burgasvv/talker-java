package org.burgas.talkerjava.service;

import jakarta.servlet.http.Part;
import org.burgas.talkerjava.cache.CacheUtil;
import org.burgas.talkerjava.cache.RedisCacheHandler;
import org.burgas.talkerjava.dao.comment.Comment;
import org.burgas.talkerjava.dao.community.Community;
import org.burgas.talkerjava.dao.identity.Identity;
import org.burgas.talkerjava.dao.publication.Publication;
import org.burgas.talkerjava.dao.publication.PublicationFile;
import org.burgas.talkerjava.dao.publication.PublicationImage;
import org.burgas.talkerjava.dto.comment.CommentFullResponse;
import org.burgas.talkerjava.dto.community.CommunityFullResponse;
import org.burgas.talkerjava.dto.identity.IdentityFullResponse;
import org.burgas.talkerjava.dto.publication.PublicationFullResponse;
import org.burgas.talkerjava.dto.publication.PublicationRequest;
import org.burgas.talkerjava.mapper.PublicationFileMapper;
import org.burgas.talkerjava.mapper.PublicationImageMapper;
import org.burgas.talkerjava.mapper.PublicationMapper;
import org.burgas.talkerjava.service.dao.DesignPartDao;
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
public class PublicationService implements RedisCacheHandler<Publication>, ReadDao<UUID, Publication, PublicationFullResponse>,
        DesignPartDao<UUID, PublicationRequest, PublicationFullResponse> {

    private final PublicationMapper publicationMapper;
    private final PublicationFileMapper publicationFileMapper;
    private final PublicationImageMapper publicationImageMapper;

    @Qualifier(value = "publicationRedisTemplate")
    private final RedisTemplate<String, PublicationFullResponse> publicationRedisTemplate;

    @Qualifier(value = "communityRedisTemplate")
    private final RedisTemplate<String, CommunityFullResponse> communityRedisTemplate;

    @Qualifier(value = "identityRedisTemplate")
    private final RedisTemplate<String, IdentityFullResponse> identityRedisTemplate;

    @Qualifier(value = "commentRedisTemplate")
    private final RedisTemplate<String, CommentFullResponse> commentRedisTemplate;

    public PublicationService(
            PublicationMapper publicationMapper, PublicationFileMapper publicationFileMapper, PublicationImageMapper publicationImageMapper,
            RedisTemplate<String, PublicationFullResponse> publicationRedisTemplate,
            RedisTemplate<String, CommunityFullResponse> communityRedisTemplate,
            RedisTemplate<String, IdentityFullResponse> identityRedisTemplate,
            RedisTemplate<String, CommentFullResponse> commentRedisTemplate
    ) {
        this.publicationMapper = publicationMapper;
        this.publicationFileMapper = publicationFileMapper;
        this.publicationImageMapper = publicationImageMapper;
        this.publicationRedisTemplate = publicationRedisTemplate;
        this.communityRedisTemplate = communityRedisTemplate;
        this.identityRedisTemplate = identityRedisTemplate;
        this.commentRedisTemplate = commentRedisTemplate;
    }

    @Override
    public void handleCache(Publication entity) {
        String publicationKey = String.format(CacheUtil.PUBLICATION_KEY, entity.getId());
        if (publicationRedisTemplate.hasKey(publicationKey)) publicationRedisTemplate.delete(publicationKey);

        Community community = entity.getCommunity();
        if (community != null) {
            String communityKey = String.format(CacheUtil.COMMUNITY_KEY, community.getId());
            if (communityRedisTemplate.hasKey(communityKey)) communityRedisTemplate.delete(communityKey);
        }

        Identity sender = entity.getSender();
        if (sender != null) {
            String senderKey = String.format(CacheUtil.IDENTITY_KEY, sender.getId());
            if (identityRedisTemplate.hasKey(senderKey)) identityRedisTemplate.delete(senderKey);
        }

        Set<Comment> comments = entity.getComments();
        if (!comments.isEmpty()) {
            comments.forEach(comment -> {
                String commentKey = String.format(CacheUtil.COMMENT_KEY, comment.getId());
                if (commentRedisTemplate.hasKey(commentKey)) commentRedisTemplate.delete(commentKey);
            });
        }
    }

    @Override
    public Publication findEntity(UUID uuid) {
        return publicationMapper.publicationRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Publication not found"));
    }

    @Override
    public PublicationFullResponse findById(UUID uuid) {
        String publicationKey = String.format(CacheUtil.PUBLICATION_KEY, uuid);
        if (publicationRedisTemplate.hasKey(publicationKey)) {
            return publicationRedisTemplate.opsForValue().get(publicationKey);
        } else {
            PublicationFullResponse publicationFullResponse = publicationMapper.toFullResponse(findEntity(uuid));
            publicationRedisTemplate.opsForValue().set(publicationKey, publicationFullResponse);
            return publicationFullResponse;
        }
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Throwable.class, Exception.class, RuntimeException.class}
    )
    public PublicationFullResponse create(PublicationRequest request, List<Part> files) {
        Publication entity = publicationMapper.toEntity(request);
        Publication publication = publicationMapper.publicationRepository.save(entity);
        files.forEach(
                part -> {
                    if (part.getContentType().startsWith("image")) {
                        PublicationImage upload = publicationImageMapper.upload(publication, part);
                        publication.getImages().add(upload);
                    } else {
                        PublicationFile upload = publicationFileMapper.upload(publication, part);
                        publication.getFiles().add(upload);
                    }
                }
        );
        return findById(publication.getId());
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Throwable.class, Exception.class, RuntimeException.class}
    )
    public void delete(UUID uuid) {
        Publication publication = findEntity(uuid);
        publicationMapper.publicationRepository.delete(publication);
        handleCache(publication);
    }
}
