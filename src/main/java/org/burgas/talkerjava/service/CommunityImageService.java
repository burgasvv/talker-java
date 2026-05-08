package org.burgas.talkerjava.service;

import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dao.community.Community;
import org.burgas.talkerjava.dao.community.CommunityImage;
import org.burgas.talkerjava.dto.document.DocumentRequest;
import org.burgas.talkerjava.dto.document.ImageRequest;
import org.burgas.talkerjava.mapper.CommunityImageMapper;
import org.burgas.talkerjava.service.document.DesignDocument;
import org.burgas.talkerjava.service.document.ModifyImage;
import org.burgas.talkerjava.service.document.ReadDocument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
public class CommunityImageService implements ReadDocument<UUID, CommunityImage>, DesignDocument<UUID>, ModifyImage<ImageRequest> {

    private final CommunityImageMapper communityImageMapper;
    private final CommunityService communityService;

    @Override
    public CommunityImage findEntity(UUID uuid) {
        return communityImageMapper.communityImageRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Community image not found"));
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Throwable.class, Exception.class, RuntimeException.class}
    )
    public void create(UUID entityId, List<Part> parts) {
        Community community = communityService.findEntity(entityId);
        parts.forEach(part -> communityImageMapper.upload(community, part));
        communityService.handleCache(community);
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Throwable.class, Exception.class, RuntimeException.class}
    )
    public void delete(UUID entityId, DocumentRequest documentRequest) {
        Community community = communityService.findEntity(entityId);
        if (!community.getImages().isEmpty()) {
            Set<UUID> imageIds = community.getImages().parallelStream().map(CommunityImage::getId).collect(Collectors.toSet());
            if (imageIds.containsAll(documentRequest.getDocumentIds())) {
                communityImageMapper.communityImageRepository.deleteAll(
                        communityImageMapper.communityImageRepository.findAllById(documentRequest.getDocumentIds())
                );
                communityService.handleCache(community);
            } else {
                throw new IllegalArgumentException("Community images not contains this images");
            }
        } else {
            throw new IllegalArgumentException("Community images empty list");
        }
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Throwable.class, Exception.class, RuntimeException.class}
    )
    public void makePreview(ImageRequest request) {
        Community community = communityService.findEntity(Objects.requireNonNull(request.getEntityId()));
        CommunityImage image = findEntity(Objects.requireNonNull(request.getImageId()));
        Set<UUID> imageIds = community.getImages().parallelStream().map(CommunityImage::getId).collect(Collectors.toSet());
        if (imageIds.contains(image.getId())) {
            community.getImages().parallelStream().filter(CommunityImage::getPreview)
                    .forEach(communityImage -> communityImage.setPreview(false));
            image.setPreview(true);
            communityService.handleCache(community);
        } else {
            throw new IllegalArgumentException("Image not belongs to this community");
        }
    }
}
