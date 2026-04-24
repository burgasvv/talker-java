package org.burgas.talkerjava.repository;

import org.burgas.talkerjava.dao.identity.IdentityImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IdentityImageRepository extends JpaRepository<IdentityImage, UUID> {
}
