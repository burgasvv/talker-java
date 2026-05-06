package org.burgas.talkerjava.dao.message;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.talkerjava.dao.Dao;
import org.burgas.talkerjava.dao.chat.Chat;
import org.burgas.talkerjava.dao.identity.Identity;

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
@Table(name = "message", schema = "public")
@NamedEntityGraph(
        name = "message-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "chat", subgraph = "chat-subgraph"),
                @NamedAttributeNode(value = "sender", subgraph = "sender-subgraph"),
                @NamedAttributeNode(value = "files"),
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "chat-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "images")
                        }
                ),
                @NamedSubgraph(
                        name = "chat-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "admin", subgraph = "chat-admin-subgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "chat-admin-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "images")
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
public class Message implements Dao {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", referencedColumnName = "id")
    private Chat chat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private Identity sender;

    @Column(name = "text")
    private String text;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MessageFile> files = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
