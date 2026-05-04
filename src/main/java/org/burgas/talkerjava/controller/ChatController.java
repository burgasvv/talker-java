package org.burgas.talkerjava.controller;

import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dto.chat.ChatFullResponse;
import org.burgas.talkerjava.dto.chat.ChatRequest;
import org.burgas.talkerjava.dto.chat.ChatShortResponse;
import org.burgas.talkerjava.dto.group.GroupRequest;
import org.burgas.talkerjava.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatShortResponse>> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(chatService.findAll());
    }

    @GetMapping("/by-id")
    public ResponseEntity<ChatFullResponse> getById(@RequestParam UUID chatId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(chatService.findById(chatId));
    }

    @PostMapping("/create")
    public ResponseEntity<Void> create(@RequestBody ChatRequest chatRequest) {
        ChatFullResponse chatFullResponse = chatService.create(chatRequest);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create("/api/v1/chats/by-id?chatId=" + chatFullResponse.getId()))
                .build();
    }

    @PostMapping("/update")
    public ResponseEntity<Void> update(@RequestBody ChatRequest chatRequest) {
        ChatFullResponse chatFullResponse = chatService.update(chatRequest);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create("/api/v1/chats/by-id?chatId=" + chatFullResponse.getId()))
                .build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam UUID chatId) {
        chatService.delete(chatId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/join")
    public ResponseEntity<Void> join(@RequestBody GroupRequest groupRequest) {
        chatService.join(groupRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/out")
    public ResponseEntity<Void> out(@RequestBody GroupRequest groupRequest) {
        chatService.out(groupRequest);
        return ResponseEntity.noContent().build();
    }
}
