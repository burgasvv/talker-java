package org.burgas.talkerjava.repository;

import org.burgas.talkerjava.dao.comment.CommentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentFileRepository extends JpaRepository<CommentFile, UUID> {
}
