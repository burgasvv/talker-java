package org.burgas.talkerjava.dto.community;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.burgas.talkerjava.dao.community.CommunityImage;
import org.burgas.talkerjava.dto.Response;
import org.burgas.talkerjava.dto.identity.IdentityShortResponse;
import org.burgas.talkerjava.dto.publication.PublicationShortResponse;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityFullResponse implements Response {

    private UUID id;
    private String name;
    private String description;
    private IdentityShortResponse admin;
    private List<CommunityImage> images;
    private List<IdentityShortResponse> identities;
    private List<PublicationShortResponse> publications;
    private String createdAt;
}
