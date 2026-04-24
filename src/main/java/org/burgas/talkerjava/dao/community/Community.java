package org.burgas.talkerjava.dao.community;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.talkerjava.dao.Dao;
import org.burgas.talkerjava.dao.identity.Identity;
import org.burgas.talkerjava.dao.publication.Publication;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "community", schema = "public")
@NamedEntityGraph(
        name = "community-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "admin", subgraph = "admin-subgraph"),
                @NamedAttributeNode(value = "images"),
                @NamedAttributeNode(value = "publications", subgraph = "publications-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "admin-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "images")
                        }
                ),
                @NamedSubgraph(
                        name = "publications-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "sender", subgraph = "publications-sender-subgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "publications-sender-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "images")
                        }
                ),
                @NamedSubgraph(
                        name = "publications-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "images")
                        }
                ),
                @NamedSubgraph(
                        name = "publications-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "files")
                        }
                )
        }
)
public class Community implements Dao {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", referencedColumnName = "id")
    private Identity admin;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommunityImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Publication> publications = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
