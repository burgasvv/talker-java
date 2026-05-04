package org.burgas.talkerjava.mapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.burgas.talkerjava.dao.publication.Publication;
import org.burgas.talkerjava.dao.publication.PublicationImage;
import org.burgas.talkerjava.mapper.contract.Uploader;
import org.burgas.talkerjava.repository.PublicationImageRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PublicationImageMapper implements Uploader<Publication, PublicationImage> {

    final PublicationImageRepository publicationImageRepository;

    @SneakyThrows
    @Override
    public PublicationImage upload(Publication entity, MultipartFile part) {
        if (Objects.requireNonNull(part.getContentType()).startsWith("image")) {
            var publicationImage = PublicationImage.builder()
                    .name(part.getOriginalFilename())
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
