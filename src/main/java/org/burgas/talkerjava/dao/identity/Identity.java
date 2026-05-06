package org.burgas.talkerjava.dao.identity;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.talkerjava.dao.Dao;
import org.burgas.talkerjava.dao.chat.Chat;
import org.burgas.talkerjava.dao.community.Community;

import java.util.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "identity", schema = "public")
@NamedEntityGraph(
        name = "identity-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "images"),
                @NamedAttributeNode(value = "chats", subgraph = "chats-subgraph"),
                @NamedAttributeNode(value = "communities", subgraph = "communities-subgraph"),
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "chats-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "images")
                        }
                ),
                @NamedSubgraph(
                        name = "chats-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "admin", subgraph = "chats-admin-subgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "chats-admin-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "images")
                        }
                ),
                @NamedSubgraph(
                        name = "communities-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "images")
                        }
                ),
                @NamedSubgraph(
                        name = "communities-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "admin", subgraph = "communities-admin-subgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "communities-admin-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "images")
                        }
                )
        }
)
public class Identity implements Dao {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "authority")
    @Enumerated(value = EnumType.STRING)
    private Authority authority;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "patronymic")
    private String patronymic;

    @OneToMany(mappedBy = "identity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<IdentityImage> images = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "identity_chat",
            joinColumns = {
                    @JoinColumn(name = "identity_id", referencedColumnName = "id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "chat_id", referencedColumnName = "id")
            }
    )
    private Set<Chat> chats = new HashSet<>();

    public void addChat(Chat chat) {
        this.chats.add(chat);
        chat.getIdentities().add(this);
    }

    public void removeChat(Chat chat) {
        this.chats.remove(chat);
        chat.getIdentities().remove(this);
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "identity_community",
            joinColumns = {
                    @JoinColumn(name = "identity_id", referencedColumnName = "id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "community_id", referencedColumnName = "id")
            }
    )
    private Set<Community> communities = new HashSet<>();

    public void addCommunity(Community community) {
        this.getCommunities().add(community);
        community.getIdentities().add(this);
    }

    public void removeCommunity(Community community) {
        this.communities.remove(community);
        community.getIdentities().remove(this);
    }
}