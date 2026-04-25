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

@Component
@RequiredArgsConstructor
public class ChatMapper implements Mapper<ChatRequest, Chat, ChatShortResponse, ChatFullResponse> {

    final ChatRepository chatRepository;

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
        return this.chatRepository.findById(request.getId())
                .map(
                        chat -> {
                            var updateChat = new Chat();
                            updateChat.setId(chat.getId());
                            var admin = this.getidentityMapper().identityRepository
                                    .findById(request.getAdminId())
                                    .orElse(null);
                            if (admin != null && chat.getIdentities().contains(admin)) {
                                updateChat.setAdmin(admin);
                            }
                            updateChat.setName(handleData(request.getName(), chat.getName()));
                            updateChat.setDescription(handleData(request.getDescription(), chat.getDescription()));
                            updateChat.setCreatedAt(chat.getCreatedAt());
                            return this.chatRepository.save(updateChat);
                        }
                )
                .orElseGet(
                        () -> {
                            var admin = this.getidentityMapper().identityRepository
                                    .findById(request.getAdminId())
                                    .orElse(null);
                            var chat = Chat.builder()
                                    .name(handleDataException(request.getName(), "Name is null"))
                                    .description(handleDataException(request.getDescription(), "Description is null"))
                                    .admin(handleDataException(admin, "Admin is null"))
                                    .createdAt(LocalDateTime.now())
                                    .build();
                            chat = this.chatRepository.save(chat);
                            if (admin != null) {
                                admin.getChats().add(chat);
                            }
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
                        Optional.ofNullable(entity.getIdentities())
                                .map(identities -> identities.parallelStream()
                                        .map(identity -> getidentityMapper().toShortResponse(identity)).toList())
                                .orElseGet(ArrayList::new)
                )
                .messages(
                        Optional.ofNullable(entity.getMessages())
                                .map(messages -> messages.parallelStream()
                                        .map(message -> getMessageMapper().toShortResponse(message)).toList())
                                .orElseGet(ArrayList::new)
                )
                .createdAt(entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm")))
                .build();
    }
}
