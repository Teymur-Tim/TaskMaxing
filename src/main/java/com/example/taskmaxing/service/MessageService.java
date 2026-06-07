package com.example.taskmaxing.service;

import com.example.taskmaxing.model.dto.request.SendMessageRequest;
import com.example.taskmaxing.model.dto.response.ConversationResponse;
import com.example.taskmaxing.model.dto.response.MessageResponse;

import java.util.List;

public interface MessageService {

    List<MessageResponse> getMessages(Long taskId, Long afterId, String username);

    MessageResponse sendMessage(Long taskId, SendMessageRequest request, String username);

    List<ConversationResponse> getMyConversations(String username);

    void purgeMessagesOfOldDoneTasks();
}
