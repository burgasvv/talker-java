package org.burgas.talkerjava.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dao.message.Message;
import org.burgas.talkerjava.dto.message.MessageFullResponse;
import org.burgas.talkerjava.dto.message.MessageRequest;
import org.burgas.talkerjava.dto.message.MessageShortResponse;
import org.burgas.talkerjava.mapper.contract.Mapper;
import org.burgas.talkerjava.repository.MessageRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessageMapper implements Mapper<MessageRequest, Message, MessageShortResponse, MessageFullResponse> {

    final MessageRepository messageRepository;

    private final ObjectFactory<IdentityMapper> identityMapperObjectFactory;
    private final ObjectFactory<ChatMapper> chatMapperObjectFactory;

    private IdentityMapper getIdentityMapper() {
        return this.identityMapperObjectFactory.getObject();
    }

    private ChatMapper getChatMapper() {
        return this.chatMapperObjectFactory.getObject();
    }

    @Override
    public Message toEntity(MessageRequest request) {
        var chat = getChatMapper().chatRepository
                .findById(handleData(request.getChatId(), new UUID(0,0)))
                .orElse(null);
        var sender = getIdentityMapper().identityRepository
                .findById(handleData(request.getSenderId(), new UUID(0,0)))
                .orElse(null);
        return Message.builder()
                .chat(handleDataException(chat, "Chat is null"))
                .sender(handleDataException(sender, "Sender is null"))
                .text(handleDataException(request.getText(), "Text is null"))
                .files(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    public MessageShortResponse toShortResponse(Message entity) {
        return MessageShortResponse.builder()
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
    public MessageFullResponse toFullResponse(Message entity) {
        return MessageFullResponse.builder()
                .id(entity.getId())
                .chat(
                        Optional.ofNullable(entity.getChat())
                                .map(chat -> getChatMapper().toShortResponse(chat))
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
