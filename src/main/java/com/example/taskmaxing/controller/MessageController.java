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

@RestController
@RequestMapping("/tasks/{taskId}/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

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
