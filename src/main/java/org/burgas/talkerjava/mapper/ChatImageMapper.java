package org.burgas.talkerjava.mapper;

import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.burgas.talkerjava.dao.chat.Chat;
import org.burgas.talkerjava.dao.chat.ChatImage;
import org.burgas.talkerjava.mapper.contract.Uploader;
import org.burgas.talkerjava.repository.ChatImageRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ChatImageMapper implements Uploader<Chat, ChatImage> {

    public final ChatImageRepository chatImageRepository;

    @SneakyThrows
    @Override
    public ChatImage upload(Chat entity, Part part) {
        if (Objects.requireNonNull(part.getContentType()).startsWith("image")) {
            var chatImage = ChatImage.builder()
                    .name(part.getSubmittedFileName())
                    .contentType(part.getContentType())
                    .data(part.getInputStream().readAllBytes())
                    .size(part.getSize())
                    .preview(false)
                    .chat(entity)
                    .build();
            return this.chatImageRepository.save(chatImage);
        } else {
            throw new IllegalArgumentException("Wrong content type");
        }
    }
}
