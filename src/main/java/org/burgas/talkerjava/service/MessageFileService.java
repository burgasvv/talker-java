package org.burgas.talkerjava.service;

import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dao.message.MessageFile;
import org.burgas.talkerjava.mapper.MessageFileMapper;
import org.burgas.talkerjava.service.document.ReadDocument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
public class MessageFileService implements ReadDocument<UUID, MessageFile> {

    private final MessageFileMapper messageFileMapper;

    @Override
    public MessageFile findEntity(UUID uuid) {
        return messageFileMapper.messageFileRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Message file not found"));
    }
}
