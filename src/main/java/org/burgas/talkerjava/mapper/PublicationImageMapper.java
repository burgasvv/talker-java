package org.burgas.talkerjava.mapper;

import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.burgas.talkerjava.dao.publication.Publication;
import org.burgas.talkerjava.dao.publication.PublicationImage;
import org.burgas.talkerjava.mapper.contract.Uploader;
import org.burgas.talkerjava.repository.PublicationImageRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublicationImageMapper implements Uploader<Publication, PublicationImage> {

    final PublicationImageRepository publicationImageRepository;

    @SneakyThrows
    @Override
    public PublicationImage upload(Publication entity, Part part) {
        if (part.getContentType().startsWith("image")) {
            var publicationImage = PublicationImage.builder()
                    .name(part.getSubmittedFileName())
                    .contentType(part.getContentType())
                    .size(part.getSize())
                    .preview(false)
                    .data(part.getInputStream().readAllBytes())
                    .publication(entity)
                    .build();
            return this.publicationImageRepository.save(publicationImage);
        } else {
            throw new IllegalArgumentException("Wrong file content type");
        }
    }
}
