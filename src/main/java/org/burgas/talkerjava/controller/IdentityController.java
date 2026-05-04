package org.burgas.talkerjava.controller;

import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dto.identity.IdentityFullResponse;
import org.burgas.talkerjava.dto.identity.IdentityRequest;
import org.burgas.talkerjava.dto.identity.IdentityShortResponse;
import org.burgas.talkerjava.service.IdentityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/identities")
public class IdentityController {

    private final IdentityService identityService;

    @GetMapping
    public ResponseEntity<List<IdentityShortResponse>> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(identityService.findAll());
    }

    @GetMapping("/by-id")
    public ResponseEntity<IdentityFullResponse> getById(@RequestParam UUID identityId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(identityService.findById(identityId));
    }

    @PostMapping("/create")
    public ResponseEntity<IdentityFullResponse> create(@RequestBody IdentityRequest identityRequest) {
        IdentityFullResponse identityFullResponse = identityService.create(identityRequest);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .location(URI.create("/api/v1/identities/by-id?identityId=" + identityFullResponse.getId()))
                .body(identityFullResponse);
    }

    @PostMapping("/update")
    public ResponseEntity<IdentityFullResponse> update(@RequestBody IdentityRequest identityRequest) {
        IdentityFullResponse identityFullResponse = identityService.update(identityRequest);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .location(URI.create("/api/v1/identities/by-id?identityId=" + identityFullResponse.getId()))
                .body(identityFullResponse);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam UUID identityId) {
        identityService.delete(identityId);
        return ResponseEntity.noContent().build();
    }
}
