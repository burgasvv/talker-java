package org.burgas.talkerjava.service;

import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dao.identity.Identity;
import org.burgas.talkerjava.dao.identity.IdentityImage;
import org.burgas.talkerjava.dto.document.DocumentRequest;
import org.burgas.talkerjava.dto.document.ImageRequest;
import org.burgas.talkerjava.mapper.IdentityImageMapper;
import org.burgas.talkerjava.service.document.DesignDocument;
import org.burgas.talkerjava.service.document.ModifyImage;
import org.burgas.talkerjava.service.document.ReadDocument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
public class IdentityImageService implements ReadDocument<UUID, IdentityImage>, DesignDocument<UUID>, ModifyImage<ImageRequest> {

    private final IdentityService identityService;
    private final IdentityImageMapper identityImageMapper;

    @Override
    public IdentityImage findEntity(UUID uuid) {
        return identityImageMapper.identityImageRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Identity image not found"));
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void create(UUID entityId, List<MultipartFile> multipartFiles) {
        Identity identity = identityService.findEntity(entityId);
        multipartFiles.forEach(multipartFile -> identityImageMapper.upload(identity, multipartFile));
        identityService.handleCache(identity);
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void delete(UUID entityId, DocumentRequest documentRequest) {
        Identity identity = identityService.findEntity(entityId);
        if (!identity.getImages().isEmpty()) {
            List<UUID> imageIds = identity.getImages().stream().map(IdentityImage::getId).toList();
            if (new HashSet<>(imageIds).containsAll(documentRequest.getDocumentIds())) {
                identityImageMapper.identityImageRepository.deleteAll(
                        identityImageMapper.identityImageRepository.findAllById(documentRequest.getDocumentIds())
                );
                identityService.handleCache(identity);
            } else {
                throw new IllegalArgumentException("Images not in identity image list");
            }
        } else {
            throw new IllegalArgumentException("Identity images empty list");
        }
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void makePreview(ImageRequest request) {
        Identity identity = identityService.findEntity(Objects.requireNonNull(request.getEntityId()));
        IdentityImage image = findEntity(Objects.requireNonNull(request.getImageId()));
        List<UUID> imageIds = identity.getImages().parallelStream().map(IdentityImage::getId).toList();
        if (imageIds.contains(image.getId())) {
            identity.getImages().stream().filter(IdentityImage::getPreview)
                    .forEach(identityImage -> identityImage.setPreview(false));
            image.setPreview(true);
            identityService.handleCache(identity);
        } else {
            throw new IllegalArgumentException("Image not belongs to this identity");
        }
    }
}
