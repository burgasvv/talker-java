package org.burgas.talkerjava.dao.comment;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.talkerjava.dao.Dao;
import org.burgas.talkerjava.dao.identity.Identity;
import org.burgas.talkerjava.dao.publication.Publication;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment", schema = "public")
public class Comment extends Dao {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publication_id", referencedColumnName = "id")
    private Publication publication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private Identity sender;

    @Column(name = "text")
    private String text;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommentFile> files = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createAt;
}
