package org.burgas.talkerjava.dao.community;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.burgas.talkerjava.dao.Document;
import org.burgas.talkerjava.dao.Image;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "community_image", schema = "public")
public class CommunityImage implements Document, Image {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "size")
    private Long size;

    @Column(name = "preview")
    private Boolean preview;

    @JsonIgnore
    @Column(name = "data")
    private byte[] data;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", referencedColumnName = "id")
    private Community community;
}
