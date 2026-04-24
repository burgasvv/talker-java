package org.burgas.talkerjava.repository;

import org.burgas.talkerjava.dao.message.Message;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Override
    @EntityGraph(value = "message-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NonNull Optional<Message> findById(@NonNull UUID uuid);
}
