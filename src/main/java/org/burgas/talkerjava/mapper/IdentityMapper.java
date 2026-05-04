package org.burgas.talkerjava.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dao.identity.Identity;
import org.burgas.talkerjava.dto.identity.IdentityFullResponse;
import org.burgas.talkerjava.dto.identity.IdentityRequest;
import org.burgas.talkerjava.dto.identity.IdentityShortResponse;
import org.burgas.talkerjava.mapper.contract.Mapper;
import org.burgas.talkerjava.repository.IdentityRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IdentityMapper implements Mapper<IdentityRequest, Identity, IdentityShortResponse, IdentityFullResponse> {

    public final IdentityRepository identityRepository;

    private final ObjectFactory<ChatMapper> chatMapperObjectFactory;
    private final ObjectFactory<CommunityMapper> communityMapperObjectFactory;

    private ChatMapper getChatMapper() {
        return this.chatMapperObjectFactory.getObject();
    }

    private CommunityMapper getCommunityMapper() {
        return this.communityMapperObjectFactory.getObject();
    }

    @Override
    public Identity toEntity(IdentityRequest request) {
        return this.identityRepository.findById(handleData(request.getId(), new UUID(0, 0)))
                .map(
                        identity -> Identity.builder()
                                .id(identity.getId())
                                .authority(handleData(request.getAuthority(), identity.getAuthority()))
                                .username(handleData(request.getUsername(), identity.getUsername()))
                                .password(identity.getPassword())
                                .email(handleData(request.getEmail(), identity.getEmail()))
                                .status(identity.getStatus())
                                .firstname(handleData(request.getFirstname(), identity.getFirstname()))
                                .lastname(handleData(request.getLastname(), identity.getLastname()))
                                .patronymic(handleData(request.getPatronymic(), identity.getPatronymic()))
                                .build()
                )
                .orElseGet(
                        () -> Identity.builder()
                                .authority(handleDataException(request.getAuthority(), "Authority is null"))
                                .username(handleDataException(request.getUsername(), "Username is null"))
                                .password(handleDataException(request.getPassword(), "Password is null"))
                                .email(handleDataException(request.getEmail(), "Email is null"))
                                .status(handleData(request.getStatus(), true))
                                .firstname(handleDataException(request.getFirstname(), "Firstname is null"))
                                .lastname(handleDataException(request.getLastname(), "Lastname is null"))
                                .patronymic(handleDataException(request.getPatronymic(), "Patronymic is null"))
                                .build()
                );
    }

    @Override
    public IdentityShortResponse toShortResponse(Identity entity) {
        return IdentityShortResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .firstname(entity.getFirstname())
                .lastname(entity.getLastname())
                .patronymic(entity.getPatronymic())
                .images(entity.getImages())
                .build();
    }

    @Override
    public IdentityFullResponse toFullResponse(Identity entity) {
        return IdentityFullResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .firstname(entity.getFirstname())
                .lastname(entity.getLastname())
                .patronymic(entity.getPatronymic())
                .images(entity.getImages())
                .chats(
                        Optional.ofNullable(entity.getChats())
                                .map(chats -> chats.parallelStream()
                                        .map(chat -> getChatMapper().toShortResponse(chat)).toList())
                                .orElseGet(ArrayList::new)
                )
                .communities(
                        Optional.ofNullable(entity.getCommunities())
                                .map(communities -> communities.parallelStream()
                                        .map(community -> getCommunityMapper().toShortResponse(community)).toList())
                                .orElseGet(ArrayList::new)
                )
                .build();
    }
}
