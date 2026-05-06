package org.burgas.talkerjava.dao.comment;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.talkerjava.dao.Dao;
import org.burgas.talkerjava.dao.identity.Identity;
import org.burgas.talkerjava.dao.publication.Publication;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment", schema = "public")
@NamedEntityGraph(
        name = "comment-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "publication", subgraph = "publication-subgraph"),
                @NamedAttributeNode(value = "sender", subgraph = "sender-subgraph"),
                @NamedAttributeNode(value = "files"),
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "publication-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "sender", subgraph = "publication-sender-subgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "publication-sender-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "images")
                        }
                ),
                @NamedSubgraph(
                        name = "publication-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "images")
                        }
                ),
                @NamedSubgraph(
                        name = "publication-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "files")
                        }
                ),
                @NamedSubgraph(
                        name = "sender-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "images")
                        }
                )
        }
)
public class Comment implements Dao {

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
    private Set<CommentFile> files = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
