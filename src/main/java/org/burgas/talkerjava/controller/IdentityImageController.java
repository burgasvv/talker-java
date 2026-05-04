package org.burgas.talkerjava.controller;

import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dao.identity.IdentityImage;
import org.burgas.talkerjava.dto.document.DocumentRequest;
import org.burgas.talkerjava.dto.document.ImageRequest;
import org.burgas.talkerjava.service.IdentityImageService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/identity-images")
public class IdentityImageController {

    private final IdentityImageService identityImageService;

    @GetMapping("/by-id")
    public ResponseEntity<Resource> getById(@RequestParam UUID imageId) {
        IdentityImage image = identityImageService.findEntity(imageId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .body(new InputStreamResource(new ByteArrayInputStream(image.getData())));
    }

    @PostMapping("/create")
    public ResponseEntity<Void> create(@RequestParam UUID identityId, @RequestPart("image") List<MultipartFile> images) {
        identityImageService.create(identityId, images);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam UUID identityId, @RequestBody DocumentRequest documentRequest) {
        identityImageService.delete(identityId, documentRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/make-preview")
    public ResponseEntity<Void> makePreview(@RequestBody ImageRequest imageRequest) {
        identityImageService.makePreview(imageRequest);
        return ResponseEntity.noContent().build();
    }
}
