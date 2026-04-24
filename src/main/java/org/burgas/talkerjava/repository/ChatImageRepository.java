package org.burgas.talkerjava.repository;

import org.burgas.talkerjava.dao.chat.ChatImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatImageRepository extends JpaRepository<ChatImage, UUID> {
}
