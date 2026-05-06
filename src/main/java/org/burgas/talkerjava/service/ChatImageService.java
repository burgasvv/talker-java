package org.burgas.talkerjava.service;

import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dao.chat.Chat;
import org.burgas.talkerjava.dao.chat.ChatImage;
import org.burgas.talkerjava.dto.document.DocumentRequest;
import org.burgas.talkerjava.dto.document.ImageRequest;
import org.burgas.talkerjava.mapper.ChatImageMapper;
import org.burgas.talkerjava.service.document.DesignDocument;
import org.burgas.talkerjava.service.document.ModifyImage;
import org.burgas.talkerjava.service.document.ReadDocument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
public class ChatImageService implements ReadDocument<UUID, ChatImage>, DesignDocument<UUID>, ModifyImage<ImageRequest> {

    private final ChatImageMapper chatImageMapper;
    private final ChatService chatService;

    @Override
    public ChatImage findEntity(UUID uuid) {
        return chatImageMapper.chatImageRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Chat Image not found"));
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void create(UUID entityId, List<Part> parts) {
        Chat chat = chatService.findEntity(Objects.requireNonNull(entityId));
        parts.forEach(part -> chatImageMapper.upload(chat, part));
        chatService.handleCache(chat);
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void delete(UUID entityId, DocumentRequest documentRequest) {
        Chat chat = chatService.findEntity(Objects.requireNonNull(entityId));
        List<UUID> imageIds = chat.getImages().parallelStream().map(ChatImage::getId).toList();
        if (new HashSet<>(imageIds).containsAll(documentRequest.getDocumentIds())) {
            chatImageMapper.chatImageRepository.deleteAll(
                    chatImageMapper.chatImageRepository.findAllById(documentRequest.getDocumentIds())
            );
            chatService.handleCache(chat);
        } else {
            throw new IllegalArgumentException("Images not in list of chat");
        }
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void makePreview(ImageRequest request) {
        Chat chat = chatService.findEntity(Objects.requireNonNull(request.getEntityId()));
        ChatImage image = findEntity(Objects.requireNonNull(request.getImageId()));
        List<UUID> imageIds = chat.getImages().parallelStream().map(ChatImage::getId).toList();
        if (imageIds.contains(image.getId())) {
            chat.getImages().parallelStream().filter(ChatImage::getPreview)
                    .forEach(chatImage -> chatImage.setPreview(false));
            image.setPreview(true);
            chatService.handleCache(chat);
        } else {
            throw new IllegalArgumentException("Image not in list of chat");
        }
    }
}
