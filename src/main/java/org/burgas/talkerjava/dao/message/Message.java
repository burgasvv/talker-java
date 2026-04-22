package org.burgas.talkerjava.dao.message;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.talkerjava.dao.Dao;
import org.burgas.talkerjava.dao.chat.Chat;
import org.burgas.talkerjava.dao.identity.Identity;

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
@Table(name = "message", schema = "public")
public class Message extends Dao {

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
    private List<MessageFile> files = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
