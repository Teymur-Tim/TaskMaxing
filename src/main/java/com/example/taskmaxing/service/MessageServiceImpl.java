package com.example.taskmaxing.service;

import com.example.taskmaxing.GlobalErroring.ForbiddenException;
import com.example.taskmaxing.GlobalErroring.ResourceNotFoundException;
import com.example.taskmaxing.mapper.MessageMapper;
import com.example.taskmaxing.model.dto.request.SendMessageRequest;
import com.example.taskmaxing.model.dto.response.ConversationResponse;
import com.example.taskmaxing.model.dto.response.MessageResponse;
import com.example.taskmaxing.model.entity.Message;
import com.example.taskmaxing.model.entity.Task;
import com.example.taskmaxing.model.entity.User;
import com.example.taskmaxing.model.enums.TaskStatus;
import com.example.taskmaxing.repository.MessageRepository;
import com.example.taskmaxing.repository.TaskRepository;
import com.example.taskmaxing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    // İş bitdikdən (DONE) bu müddət sonra çat mesajları DB-dən silinir. Tapşırıq
    // özü tarixçə kimi qalır. (Sonra serverdə təhlükəsizlik üçün saxlamaq istəsən,
    // bu dəyəri artır və ya təmizləməni söndür.)
    private static final Duration MESSAGE_RETENTION_AFTER_DONE = Duration.ofDays(3);

    private final MessageRepository messageRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(Long taskId, Long afterId, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tapşırıq tapılmadı! ID: " + taskId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı: " + username));

        validateParticipant(task, user);

        // afterId verilibsə yalnız yeni mesajlar (delta), yoxsa bütün söhbət.
        List<Message> messages = (afterId != null)
                ? messageRepository.findByTaskIdAndIdGreaterThanOrderByIdAsc(taskId, afterId)
                : messageRepository.findByTaskIdOrderByIdAsc(taskId);

        return messages.stream().map(messageMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public MessageResponse sendMessage(Long taskId, SendMessageRequest request, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tapşırıq tapılmadı! ID: " + taskId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı: " + username));

        validateParticipant(task, user);

        Message message = new Message();
        message.setTask(task);
        message.setSender(user);
        message.setContent(request.content().trim());
        message.setCreatedAt(Instant.now());

        return messageMapper.toResponse(messageRepository.save(message));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getMyConversations(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı: " + username));

        // İştirak etdiyim, icraçısı olan tapşırıqlar: yaratdıqlarım + götürdüklərim.
        // LinkedHashMap ilə təkrarları (id-yə görə) atırıq, sıra qorunur.
        Map<Long, Task> tasks = new LinkedHashMap<>();
        taskRepository.findByClientIdAndTaskerIsNotNull(user.getId())
                .forEach(t -> tasks.put(t.getId(), t));
        taskRepository.findByTaskerId(user.getId())
                .forEach(t -> tasks.put(t.getId(), t));

        List<ConversationResponse> conversations = new ArrayList<>();
        for (Task task : tasks.values()) {
            String clientName = task.getClient() != null ? task.getClient().getUsername() : null;
            String taskerName = task.getTasker() != null ? task.getTasker().getUsername() : null;

            Message last = messageRepository.findTopByTaskIdOrderByIdDesc(task.getId()).orElse(null);

            conversations.add(new ConversationResponse(
                    task.getId(),
                    task.getTitle(),
                    task.getBudget(),
                    task.getStatus() != null ? task.getStatus().name() : null,
                    clientName,
                    taskerName,
                    last != null ? last.getId() : null,
                    last != null ? last.getContent() : null,
                    last != null && last.getSender() != null ? last.getSender().getUsername() : null,
                    last != null ? last.getCreatedAt() : null
            ));
        }

        // Ən son yazışılan yuxarıda; mesajı olmayanlar (null) ən sonda.
        conversations.sort(Comparator.comparing(
                ConversationResponse::lastCreatedAt,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));
        return conversations;
    }

    @Override
    @Transactional
    public void purgeMessagesOfOldDoneTasks() {
        Instant cutoff = Instant.now().minus(MESSAGE_RETENTION_AFTER_DONE);
        messageRepository.deleteByTask_StatusAndTask_DoneAtBefore(TaskStatus.DONE, cutoff);
    }

    // Çat yalnız iki nəfərlikdir: tapşırığın client-i və tasker-i. İcraçı təyin
    // olunana qədər söhbət açılmır; kənar şəxs nə oxuya, nə yaza bilər.
    private void validateParticipant(Task task, User user) {
        if (task.getTasker() == null) {
            throw new ForbiddenException("İcraçı təyin olunana qədər bu tapşırıqda söhbət başlamır.");
        }
        Long me = user.getId();
        boolean isClient = task.getClient() != null && task.getClient().getId().equals(me);
        boolean isTasker = task.getTasker().getId().equals(me);
        if (!isClient && !isTasker) {
            throw new ForbiddenException("Bu söhbətin iştirakçısı deyilsiniz!");
        }
    }
}
