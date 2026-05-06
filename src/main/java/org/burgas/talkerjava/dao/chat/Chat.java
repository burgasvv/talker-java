package org.burgas.talkerjava.dao.chat;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.talkerjava.dao.Dao;
import org.burgas.talkerjava.dao.identity.Identity;
import org.burgas.talkerjava.dao.message.Message;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat", schema = "public")
@NamedEntityGraph(
        name = "chat-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "admin", subgraph = "admin-subgraph"),
                @NamedAttributeNode(value = "images"),
                @NamedAttributeNode(value = "identities", subgraph = "identities-subgraph"),
                @NamedAttributeNode(value = "messages", subgraph = "messages-subgraph"),
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
                        name = "messages-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "sender", subgraph = "sender-subgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "messages-subgraph",
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
public class Chat implements Dao {

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

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ChatImage> images = new HashSet<>();

    @ManyToMany(mappedBy = "chats", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Identity> identities = new HashSet<>();

    public void addIdentity(Identity identity) {
        this.getIdentities().add(identity);
        identity.getChats().add(this);
    }

    public void removeIdentity(Identity identity) {
        this.getIdentities().remove(identity);
        identity.getChats().remove(this);
    }

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Message> messages = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
