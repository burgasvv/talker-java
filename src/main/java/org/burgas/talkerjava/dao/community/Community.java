package org.burgas.talkerjava.dao.community;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.talkerjava.dao.Dao;
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
@Table(name = "community", schema = "public")
public class Community extends Dao {

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

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
