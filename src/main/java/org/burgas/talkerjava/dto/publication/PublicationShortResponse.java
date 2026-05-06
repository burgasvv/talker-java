package org.burgas.talkerjava.dto.publication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.burgas.talkerjava.dao.publication.PublicationFile;
import org.burgas.talkerjava.dao.publication.PublicationImage;
import org.burgas.talkerjava.dto.Response;
import org.burgas.talkerjava.dto.identity.IdentityShortResponse;

import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicationShortResponse implements Response {

    private UUID id;
    private IdentityShortResponse sender;
    private String text;
    private Set<PublicationImage> images;
    private Set<PublicationFile> files;
    private String createdAt;
}
