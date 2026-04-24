package org.burgas.talkerjava.repository;

import org.burgas.talkerjava.dao.community.Community;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommunityRepository extends JpaRepository<Community, UUID> {

    @Override
    @EntityGraph(value = "community-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NonNull Optional<Community> findById(@NonNull UUID uuid);
}
