package org.burgas.talkerjava.repository;

import org.burgas.talkerjava.dao.chat.Chat;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {

    @Override
    @EntityGraph(value = "chat-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NonNull Optional<Chat> findById(@NonNull UUID uuid);
}
