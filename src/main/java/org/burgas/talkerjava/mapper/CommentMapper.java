package org.burgas.talkerjava.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dao.comment.Comment;
import org.burgas.talkerjava.dto.comment.CommentFullResponse;
import org.burgas.talkerjava.dto.comment.CommentRequest;
import org.burgas.talkerjava.dto.comment.CommentShortResponse;
import org.burgas.talkerjava.mapper.contract.Mapper;
import org.burgas.talkerjava.repository.CommentRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommentMapper implements Mapper<CommentRequest, Comment, CommentShortResponse, CommentFullResponse> {

    final CommentRepository commentRepository;

    private final ObjectFactory<PublicationMapper> publicationMapperObjectFactory;
    private final ObjectFactory<IdentityMapper> identityMapperObjectFactory;

    private PublicationMapper getPublicationMapper() {
        return this.publicationMapperObjectFactory.getObject();
    }

    private IdentityMapper getIdentityMapper() {
        return this.identityMapperObjectFactory.getObject();
    }

    @Override
    public Comment toEntity(CommentRequest request) {
        var sender = getIdentityMapper().identityRepository
                .findById(request.getSenderId())
                .orElse(null);
        var publication = getPublicationMapper().publicationRepository
                .findById(request.getPublicationId())
                .orElse(null);
        return Comment.builder()
                .publication(handleDataException(publication, "Publication is null"))
                .sender(handleDataException(sender, "Sender is null"))
                .text(handleDataException(request.getText(), "Text is null"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    public CommentShortResponse toShortResponse(Comment entity) {
        return CommentShortResponse.builder()
                .id(entity.getId())
                .sender(
                        Optional.ofNullable(entity.getSender())
                                .map(identity -> getIdentityMapper().toShortResponse(identity))
                                .orElse(null)
                )
                .text(entity.getText())
                .files(entity.getFiles())
                .createdAt(entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm")))
                .build();
    }

    @Override
    public CommentFullResponse toFullResponse(Comment entity) {
        return CommentFullResponse.builder()
                .id(entity.getId())
                .publication(
                        Optional.ofNullable(entity.getPublication())
                                .map(publication -> getPublicationMapper().toShortResponse(publication))
                                .orElse(null)
                )
                .sender(
                        Optional.ofNullable(entity.getSender())
                                .map(identity -> getIdentityMapper().toShortResponse(identity))
                                .orElse(null)
                )
                .text(entity.getText())
                .files(entity.getFiles())
                .createdAt(entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm")))
                .build();
    }
}
