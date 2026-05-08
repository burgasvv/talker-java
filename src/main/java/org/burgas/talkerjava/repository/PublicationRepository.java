package org.burgas.talkerjava.repository;

import org.burgas.talkerjava.dao.community.Community;
import org.burgas.talkerjava.dao.publication.Publication;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, UUID> {

    @Override
    @EntityGraph(value = "publication-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NonNull Optional<Publication> findById(@NonNull UUID uuid);

    @EntityGraph(value = "publication-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    List<Publication> findPublicationsByCommunity(Community community);
}
