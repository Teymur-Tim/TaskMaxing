package com.example.taskmaxing.controller;

import com.example.taskmaxing.model.dto.request.SendMessageRequest;
import com.example.taskmaxing.model.dto.response.MessageResponse;
import com.example.taskmaxing.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Tapşırıq daxili çat. Bütün endpointlər /tasks/** altındadır → SecurityConfig
// onsuz da token tələb edir. İştirakçı yoxlanışı servisdə aparılır.
@RestController
@RequestMapping("/tasks/{taskId}/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // GET /tasks/{taskId}/messages           -> bütün söhbət (ilk yükləmə)
    // GET /tasks/{taskId}/messages?after=42  -> yalnız 42-dən sonrakılar (delta-polling)
    @GetMapping
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable Long taskId,
            @RequestParam(required = false) Long after,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                messageService.getMessages(taskId, after, authentication.getName())
        );
    }

    // POST /tasks/{taskId}/messages -> yeni mesaj göndər
    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable Long taskId,
            @Valid @RequestBody SendMessageRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                messageService.sendMessage(taskId, request, authentication.getName())
        );
    }
}
