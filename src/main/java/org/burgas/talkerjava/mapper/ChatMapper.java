package org.burgas.talkerjava.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dao.chat.Chat;
import org.burgas.talkerjava.dto.chat.ChatFullResponse;
import org.burgas.talkerjava.dto.chat.ChatRequest;
import org.burgas.talkerjava.dto.chat.ChatShortResponse;
import org.burgas.talkerjava.mapper.contract.Mapper;
import org.burgas.talkerjava.repository.ChatRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatMapper implements Mapper<ChatRequest, Chat, ChatShortResponse, ChatFullResponse> {

    public final ChatRepository chatRepository;

    private final ObjectFactory<IdentityMapper> identityMapperObjectFactory;
    private final ObjectFactory<MessageMapper> messageMapperObjectFactory;

    private IdentityMapper getidentityMapper() {
        return this.identityMapperObjectFactory.getObject();
    }

    private MessageMapper getMessageMapper() {
        return this.messageMapperObjectFactory.getObject();
    }

    @Override
    public Chat toEntity(ChatRequest request) {
        return this.chatRepository.findById(handleData(request.getId(), new UUID(0,0)))
                .map(
                        chat -> {
                            var updateChat = new Chat();
                            updateChat.setId(chat.getId());
                            var admin = this.getidentityMapper().identityRepository
                                    .findById(handleData(request.getAdminId(), new UUID(0,0)))
                                    .orElse(null);
                            if (admin != null && chat.getIdentities().contains(admin))
                                updateChat.setAdmin(admin);
                            else
                                updateChat.setAdmin(chat.getAdmin());
                            updateChat.setName(handleData(request.getName(), chat.getName()));
                            updateChat.setDescription(handleData(request.getDescription(), chat.getDescription()));
                            updateChat.setImages(chat.getImages());
                            updateChat.setIdentities(chat.getIdentities());
                            updateChat.setMessages(chat.getMessages());
                            updateChat.setCreatedAt(chat.getCreatedAt());
                            return this.chatRepository.save(updateChat);
                        }
                )
                .orElseGet(
                        () -> {
                            var admin = this.getidentityMapper().identityRepository
                                    .findById(handleData(request.getAdminId(), new UUID(0,0)))
                                    .orElse(null);
                            var chat = Chat.builder()
                                    .name(handleDataException(request.getName(), "Name is null"))
                                    .description(handleDataException(request.getDescription(), "Description is null"))
                                    .admin(handleDataException(admin, "Admin is null"))
                                    .images(new ArrayList<>())
                                    .identities(new ArrayList<>())
                                    .messages(new ArrayList<>())
                                    .createdAt(LocalDateTime.now())
                                    .build();
                            chat = this.chatRepository.save(chat);
                            assert admin != null;
                            chat.addIdentity(admin);
                            return chat;
                        }
                );
    }

    @Override
    public ChatShortResponse toShortResponse(Chat entity) {
        return ChatShortResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .admin(
                        Optional.ofNullable(entity.getAdmin())
                                .map(identity -> getidentityMapper().toShortResponse(identity)).orElse(null)
                )
                .images(entity.getImages())
                .createdAt(entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm")))
                .build();
    }

    @Override
    public ChatFullResponse toFullResponse(Chat entity) {
        return ChatFullResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .admin(
                        Optional.ofNullable(entity.getAdmin())
                                .map(identity -> getidentityMapper().toShortResponse(identity)).orElse(null)
                )
                .images(entity.getImages())
                .identities(
                        entity.getIdentities().parallelStream()
                                .map(identity -> getidentityMapper().toShortResponse(identity)).toList()
                )
                .messages(
                        entity.getMessages().parallelStream()
                                .map(message -> getMessageMapper().toShortResponse(message)).toList()
                )
                .createdAt(entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm")))
                .build();
    }
}
