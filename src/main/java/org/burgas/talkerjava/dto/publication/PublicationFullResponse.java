package org.burgas.talkerjava.dto.publication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.burgas.talkerjava.dao.publication.PublicationFile;
import org.burgas.talkerjava.dao.publication.PublicationImage;
import org.burgas.talkerjava.dto.Response;
import org.burgas.talkerjava.dto.comment.CommentShortResponse;
import org.burgas.talkerjava.dto.community.CommunityShortResponse;
import org.burgas.talkerjava.dto.identity.IdentityShortResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicationFullResponse implements Response {

    private UUID id;
    private CommunityShortResponse community;
    private IdentityShortResponse sender;
    private String text;
    private Set<PublicationImage> images;
    private Set<PublicationFile> files;
    private Set<CommentShortResponse> comments;
    private String createdAt;
}
