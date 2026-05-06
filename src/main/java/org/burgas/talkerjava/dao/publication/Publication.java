package org.burgas.talkerjava.dao.publication;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.talkerjava.dao.Dao;
import org.burgas.talkerjava.dao.comment.Comment;
import org.burgas.talkerjava.dao.community.Community;
import org.burgas.talkerjava.dao.identity.Identity;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "publication", schema = "public")
@NamedEntityGraph(
        name = "publication-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "community", subgraph = "community-subgraph"),
                @NamedAttributeNode(value = "sender", subgraph = "sender-subgraph"),
                @NamedAttributeNode(value = "images"),
                @NamedAttributeNode(value = "files"),
                @NamedAttributeNode(value = "comments", subgraph = "comments-subgraph"),
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "community-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "images")
                        }
                ),
                @NamedSubgraph(
                        name = "community-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "admin", subgraph = "community-admin-subgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "community-admin-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "images")
                        }
                ),
                @NamedSubgraph(
                        name = "sender-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "images")
                        }
                ),
                @NamedSubgraph(
                        name = "comments-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "sender", subgraph = "comments-sender-subgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "comments-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "files")
                        }
                ),
                @NamedSubgraph(
                        name = "comments-sender-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "images")
                        }
                )
        }
)
public class Publication implements Dao {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", referencedColumnName = "id")
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private Identity sender;

    @Column(name = "text")
    private String text;

    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PublicationImage> images = new HashSet<>();

    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PublicationFile> files = new HashSet<>();

    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
