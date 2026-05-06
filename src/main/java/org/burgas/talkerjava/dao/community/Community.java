package org.burgas.talkerjava.dao.community;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.talkerjava.dao.Dao;
import org.burgas.talkerjava.dao.identity.Identity;
import org.burgas.talkerjava.dao.publication.Publication;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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
                @NamedAttributeNode(value = "identities", subgraph = "identities-subgraph"),
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
                        name = "identities-subgraph",
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
    private Set<CommunityImage> images = new HashSet<>();

    @ManyToMany(mappedBy = "communities", fetch = FetchType.LAZY)
    private Set<Identity> identities = new HashSet<>();

    public void addIdentity(Identity identity) {
        this.getIdentities().add(identity);
        identity.getCommunities().add(this);
    }

    public void removeIdentity(Identity identity) {
        this.getIdentities().remove(identity);
        identity.getCommunities().remove(this);
    }

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Publication> publications = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
