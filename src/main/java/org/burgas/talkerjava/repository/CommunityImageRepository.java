package org.burgas.talkerjava.repository;

import org.burgas.talkerjava.dao.community.CommunityImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommunityImageRepository extends JpaRepository<CommunityImage, UUID> {
}
