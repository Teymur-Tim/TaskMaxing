package com.example.taskmaxing.service;

import com.example.taskmaxing.model.dto.request.SendMessageRequest;
import com.example.taskmaxing.model.dto.response.ConversationResponse;
import com.example.taskmaxing.model.dto.response.MessageResponse;

import java.util.List;

public interface MessageService {

    // Tapşırığın çat mesajları. afterId verilibsə yalnız ondan sonrakılar (delta-polling),
    // null isə bütün söhbət (ilk yükləmə). Yalnız iştirakçı (client/tasker) görə bilər.
    List<MessageResponse> getMessages(Long taskId, Long afterId, String username);

    // Yeni mesaj göndər. Yalnız tapşırığın client-i və ya tasker-i göndərə bilər.
    MessageResponse sendMessage(Long taskId, SendMessageRequest request, String username);

    // İştirak etdiyim bütün söhbətlər (son mesajla birlikdə) — "Mesajlar" səhifəsi
    // və oxunmamış bildirişi üçün. Ən son yazışılan yuxarıda.
    List<ConversationResponse> getMyConversations(String username);

    // Bir neçə gün əvvəl bitmiş (DONE) tapşırıqların çat mesajlarını DB-dən silir.
    // Planlaşdırılmış iş tərəfindən çağırılır.
    void purgeMessagesOfOldDoneTasks();
}
