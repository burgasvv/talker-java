package org.burgas.talkerjava.repository;

import org.burgas.talkerjava.dao.comment.Comment;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @Override
    @EntityGraph(value = "comment-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NonNull Optional<Comment> findById(@NonNull UUID uuid);
}
