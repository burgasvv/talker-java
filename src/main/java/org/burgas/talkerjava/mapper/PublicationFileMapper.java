package org.burgas.talkerjava.mapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.burgas.talkerjava.dao.publication.Publication;
import org.burgas.talkerjava.dao.publication.PublicationFile;
import org.burgas.talkerjava.mapper.contract.Uploader;
import org.burgas.talkerjava.repository.PublicationFileRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class PublicationFileMapper implements Uploader<Publication, PublicationFile> {

    final PublicationFileRepository publicationFileRepository;

    @SneakyThrows
    @Override
    public PublicationFile upload(Publication entity, MultipartFile part) {
        var publicationFile = PublicationFile.builder()
                .name(part.getOriginalFilename())
                .contentType(part.getContentType())
                .size(part.getSize())
                .data(part.getInputStream().readAllBytes())
                .publication(entity)
                .build();
        return this.publicationFileRepository.save(publicationFile);
    }
}
