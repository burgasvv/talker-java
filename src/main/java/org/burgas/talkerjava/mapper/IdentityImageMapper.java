package org.burgas.talkerjava.mapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.burgas.talkerjava.dao.identity.Identity;
import org.burgas.talkerjava.dao.identity.IdentityImage;
import org.burgas.talkerjava.mapper.contract.Uploader;
import org.burgas.talkerjava.repository.IdentityImageRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class IdentityImageMapper implements Uploader<Identity, IdentityImage> {

    public final IdentityImageRepository identityImageRepository;

    @SneakyThrows
    @Override
    public IdentityImage upload(Identity entity, MultipartFile part) {
        if (Objects.requireNonNull(part.getContentType()).startsWith("image")) {
            var identityImage = IdentityImage.builder()
                    .name(part.getOriginalFilename())
                    .contentType(part.getContentType())
                    .size(part.getSize())
                    .data(part.getInputStream().readAllBytes())
                    .preview(false)
                    .identity(entity)
                    .build();
            return this.identityImageRepository.save(identityImage);
        } else {
            throw new IllegalArgumentException("Wrong file content type");
        }
    }
}
