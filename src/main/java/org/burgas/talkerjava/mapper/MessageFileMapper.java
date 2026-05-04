package org.burgas.talkerjava.mapper;

import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.burgas.talkerjava.dao.message.Message;
import org.burgas.talkerjava.dao.message.MessageFile;
import org.burgas.talkerjava.mapper.contract.Uploader;
import org.burgas.talkerjava.repository.MessageFileRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class MessageFileMapper implements Uploader<Message, MessageFile> {

    final MessageFileRepository messageFileRepository;

    @SneakyThrows
    @Override
    public MessageFile upload(Message entity, MultipartFile part) {
        var messageFile = MessageFile.builder()
                .name(part.getOriginalFilename())
                .contentType(part.getContentType())
                .size(part.getSize())
                .data(part.getInputStream().readAllBytes())
                .message(entity)
                .build();
        return this.messageFileRepository.save(messageFile);
    }
}
