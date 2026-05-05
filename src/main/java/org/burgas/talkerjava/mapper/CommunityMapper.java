package org.burgas.talkerjava.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dao.community.Community;
import org.burgas.talkerjava.dto.community.CommunityFullResponse;
import org.burgas.talkerjava.dto.community.CommunityRequest;
import org.burgas.talkerjava.dto.community.CommunityShortResponse;
import org.burgas.talkerjava.mapper.contract.Mapper;
import org.burgas.talkerjava.repository.CommunityRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CommunityMapper implements Mapper<CommunityRequest, Community, CommunityShortResponse, CommunityFullResponse> {

    final CommunityRepository communityRepository;

    private final ObjectFactory<IdentityMapper> identityMapperObjectFactory;
    private final ObjectFactory<PublicationMapper> publicationMapperObjectFactory;

    private IdentityMapper getIdentityMapper() {
        return this.identityMapperObjectFactory.getObject();
    }

    private PublicationMapper getPublicationmapper() {
        return this.publicationMapperObjectFactory.getObject();
    }

    @Override
    public Community toEntity(CommunityRequest request) {
        return this.communityRepository.findById(handleData(request.getId(), new UUID(0,0)))
                .map(
                        community -> {
                            var updateCommunity = new Community();
                            updateCommunity.setId(community.getId());
                            var admin = getIdentityMapper().identityRepository
                                    .findById(handleData(request.getAdminId(), new UUID(0,0)))
                                    .orElse(null);
                            if (admin != null && community.getIdentities().contains(admin))
                                updateCommunity.setAdmin(admin);
                            else
                                updateCommunity.setAdmin(community.getAdmin());
                            updateCommunity.setName(handleData(request.getName(), community.getName()));
                            updateCommunity.setDescription(handleData(request.getDescription(), community.getDescription()));
                            updateCommunity.setIdentities(community.getIdentities());
                            updateCommunity.setImages(community.getImages());
                            updateCommunity.setPublications(community.getPublications());
                            updateCommunity.setCreatedAt(community.getCreatedAt());
                            return this.communityRepository.save(updateCommunity);
                        }
                )
                .orElseGet(
                        () -> {
                            var admin = getIdentityMapper().identityRepository
                                    .findById(handleData(request.getAdminId(), new UUID(0,0)))
                                    .orElse(null);
                            var community = Community.builder()
                                    .name(handleDataException(request.getName(), "Name is null"))
                                    .description(handleDataException(request.getDescription(), "Description is null"))
                                    .admin(handleDataException(admin, "Admin is null"))
                                    .identities(new ArrayList<>())
                                    .images(new ArrayList<>())
                                    .publications(new ArrayList<>())
                                    .createdAt(LocalDateTime.now())
                                    .build();
                            community = this.communityRepository.save(community);
                            assert admin != null;
                            community.addIdentity(admin);
                            return community;
                        }
                );
    }

    @Override
    public CommunityShortResponse toShortResponse(Community entity) {
        return CommunityShortResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .admin(
                        Optional.ofNullable(entity.getAdmin())
                                .map(identity -> getIdentityMapper().toShortResponse(identity))
                                .orElse(null)
                )
                .images(entity.getImages())
                .createdAt(entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm")))
                .build();
    }

    @Override
    public CommunityFullResponse toFullResponse(Community entity) {
        return CommunityFullResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .admin(
                        Optional.ofNullable(entity.getAdmin())
                                .map(identity -> getIdentityMapper().toShortResponse(identity))
                                .orElse(null)
                )
                .images(entity.getImages())
                .identities(
                        entity.getIdentities().parallelStream()
                                .map(identity -> getIdentityMapper().toShortResponse(identity)).toList()
                )
                .publications(
                        entity.getPublications().parallelStream()
                                .map(publication -> getPublicationmapper().toShortResponse(publication)).toList()
                )
                .createdAt(entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm")))
                .build();
    }
}
