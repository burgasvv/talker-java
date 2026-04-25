package org.burgas.talkerjava.mapper;

import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.burgas.talkerjava.dao.community.Community;
import org.burgas.talkerjava.dao.community.CommunityImage;
import org.burgas.talkerjava.mapper.contract.Uploader;
import org.burgas.talkerjava.repository.CommunityImageRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommunityImageMapper implements Uploader<Community, CommunityImage> {

    final CommunityImageRepository communityImageRepository;

    @SneakyThrows
    @Override
    public CommunityImage upload(Community entity, Part part) {
        if (part.getContentType().startsWith("image")) {
            var communityImage = CommunityImage.builder()
                    .name(part.getSubmittedFileName())
                    .contentType(part.getContentType())
                    .size(part.getSize())
                    .preview(false)
                    .data(part.getInputStream().readAllBytes())
                    .community(entity)
                    .build();
            return this.communityImageRepository.save(communityImage);
        } else {
            throw new IllegalArgumentException("Wrong file content type");
        }
    }
}
