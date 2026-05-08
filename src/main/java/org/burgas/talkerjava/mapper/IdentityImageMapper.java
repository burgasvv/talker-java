package org.burgas.talkerjava.mapper;

import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.burgas.talkerjava.dao.identity.Identity;
import org.burgas.talkerjava.dao.identity.IdentityImage;
import org.burgas.talkerjava.mapper.contract.Uploader;
import org.burgas.talkerjava.repository.IdentityImageRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class IdentityImageMapper implements Uploader<Identity, IdentityImage> {

    public final IdentityImageRepository identityImageRepository;

    @SneakyThrows
    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public IdentityImage upload(Identity entity, Part part) {
        if (Objects.requireNonNull(part.getContentType()).startsWith("image")) {
            var identityImage = IdentityImage.builder()
                    .name(part.getSubmittedFileName())
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
