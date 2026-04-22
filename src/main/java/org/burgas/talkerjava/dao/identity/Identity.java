package org.burgas.talkerjava.dao.identity;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.talkerjava.dao.Dao;
import org.burgas.talkerjava.dao.chat.Chat;
import org.burgas.talkerjava.dao.community.Community;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                        name = "communities-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "images")
                        }
                )
        }
)
public class Identity extends Dao {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "authority")
    @Enumerated(value = EnumType.STRING)
    private Authority authority;

    @Column(name = "username")
    private String username;

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
    private List<IdentityImage> images = new ArrayList<>();

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
    private List<Chat> chats = new ArrayList<>();

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
    private List<Community> communities = new ArrayList<>();
}