package com.example.taskmaxing.controller;

import com.example.taskmaxing.model.dto.response.ConversationResponse;
import com.example.taskmaxing.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ConversationController {

    private final MessageService messageService;

    @GetMapping("/tasks/my/conversations")
    public ResponseEntity<List<ConversationResponse>> myConversations(Authentication authentication) {
        return ResponseEntity.ok(messageService.getMyConversations(authentication.getName()));
    }
}
