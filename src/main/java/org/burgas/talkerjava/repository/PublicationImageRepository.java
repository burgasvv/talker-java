package org.burgas.talkerjava.repository;

import org.burgas.talkerjava.dao.publication.PublicationImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PublicationImageRepository extends JpaRepository<PublicationImage, UUID> {
}
