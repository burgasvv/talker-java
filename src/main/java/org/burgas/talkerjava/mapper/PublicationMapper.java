package org.burgas.talkerjava.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dao.publication.Publication;
import org.burgas.talkerjava.dto.publication.PublicationFullResponse;
import org.burgas.talkerjava.dto.publication.PublicationRequest;
import org.burgas.talkerjava.dto.publication.PublicationShortResponse;
import org.burgas.talkerjava.mapper.contract.Mapper;
import org.burgas.talkerjava.repository.PublicationRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PublicationMapper implements Mapper<PublicationRequest, Publication, PublicationShortResponse, PublicationFullResponse> {

    final PublicationRepository publicationRepository;

    private final ObjectFactory<IdentityMapper> identityMapperObjectFactory;
    private final ObjectFactory<CommunityMapper> communityMapperObjectFactory;
    private final ObjectFactory<CommentMapper> commentMapperObjectFactory;

    private IdentityMapper getIdentityMapper() {
        return this.identityMapperObjectFactory.getObject();
    }

    private CommunityMapper getCommunityMapper() {
        return this.communityMapperObjectFactory.getObject();
    }

    private CommentMapper getCommentMapper() {
        return this.commentMapperObjectFactory.getObject();
    }

    @Override
    public Publication toEntity(PublicationRequest request) {
        var community = getCommunityMapper().communityRepository
                .findById(handleData(request.getCommunityId(), new UUID(0,0)))
                .orElse(null);
        var sender = getIdentityMapper().identityRepository
                .findById(handleData(request.getSenderId(), new UUID(0,0)))
                .orElse(null);
        return Publication.builder()
                .community(handleDataException(community, "Community is null"))
                .sender(handleDataException(sender, "Sender is null"))
                .text(handleDataException(request.getText(), "Text is null"))
                .comments(new HashSet<>())
                .files(new HashSet<>())
                .images(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    public PublicationShortResponse toShortResponse(Publication entity) {
        return PublicationShortResponse.builder()
                .id(entity.getId())
                .sender(
                        Optional.ofNullable(entity.getSender())
                                .map(identity -> getIdentityMapper().toShortResponse(identity))
                                .orElse(null)
                )
                .text(entity.getText())
                .images(entity.getImages())
                .files(entity.getFiles())
                .createdAt(entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm")))
                .build();
    }

    @Override
    public PublicationFullResponse toFullResponse(Publication entity) {
        return PublicationFullResponse.builder()
                .id(entity.getId())
                .community(
                        Optional.ofNullable(entity.getCommunity())
                                .map(community -> getCommunityMapper().toShortResponse(community))
                                .orElse(null)
                )
                .sender(
                        Optional.ofNullable(entity.getSender())
                                .map(identity -> getIdentityMapper().toShortResponse(identity))
                                .orElse(null)
                )
                .text(entity.getText())
                .images(entity.getImages())
                .files(entity.getFiles())
                .comments(
                        entity.getComments().parallelStream()
                                .map(comment -> getCommentMapper().toShortResponse(comment))
                                .collect(Collectors.toSet())
                )
                .createdAt(entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm")))
                .build();
    }
}
