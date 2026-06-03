package com.example.taskmaxing.service;

import com.example.taskmaxing.GlobalErroring.ConflictException;
import com.example.taskmaxing.GlobalErroring.ForbiddenException;
import com.example.taskmaxing.GlobalErroring.ResourceNotFoundException;
import com.example.taskmaxing.mapper.ReviewMapper;
import com.example.taskmaxing.model.dto.request.CreateReviewRequest;
import com.example.taskmaxing.model.dto.response.ReviewResponse;
import com.example.taskmaxing.model.entity.Review;
import com.example.taskmaxing.model.entity.Task;
import com.example.taskmaxing.model.entity.User;
import com.example.taskmaxing.model.enums.TaskStatus;
import com.example.taskmaxing.repository.ReviewRepository;
import com.example.taskmaxing.repository.TaskRepository;
import com.example.taskmaxing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    // Tamamlanmış (DONE) tapşırığın iştirakçısı qarşı tərəf haqqında rəy yazır.
    @Transactional
    public ReviewResponse createReview(Long taskId, String reviewerUsername, CreateReviewRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tapşırıq tapılmadı! ID: " + taskId));

        // Rəy yalnız iş tam bitdikdən (DONE) sonra yazıla bilər
        if (task.getStatus() != TaskStatus.DONE) {
            throw new ConflictException("Yalnız tamamlanmış (DONE) tapşırığa rəy yazmaq olar!");
        }

        User reviewer = userRepository.findByUsername(reviewerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı: " + reviewerUsername));

        User client = task.getClient();
        User tasker = task.getTasker();

        // Rəyi yazan tapşırığın iştirakçısı olmalıdır; qarşı tərəf rəyin ünvanıdır.
        User receiver;
        if (client != null && client.getId().equals(reviewer.getId())) {
            receiver = tasker;
        } else if (tasker != null && tasker.getId().equals(reviewer.getId())) {
            receiver = client;
        } else {
            throw new ForbiddenException("Yalnız bu tapşırığın iştirakçıları rəy yaza bilər!");
        }

        if (receiver == null) {
            throw new ConflictException("Bu tapşırığın qarşı tərəfi yoxdur.");
        }

        if (reviewRepository.existsByTaskIdAndReviewerId(taskId, reviewer.getId())) {
            throw new ConflictException("Bu tapşırıq üçün artıq rəy yazmısınız!");
        }

        Review review = new Review();
        review.setRating(request.rating());
        review.setComment(request.comment());
        review.setReviewer(reviewer);
        review.setReceiver(receiver);
        review.setTask(task);
        review.setCreatedAt(Instant.now());
        Review saved = reviewRepository.save(review);

        // Qarşı tərəfin reytinq aqreqatını yeniləyirik
        long count = receiver.getRatingCount() == null ? 0L : receiver.getRatingCount();
        long sum = receiver.getRatingSum() == null ? 0L : receiver.getRatingSum();
        receiver.setRatingCount(count + 1);
        receiver.setRatingSum(sum + request.rating());
        userRepository.save(receiver);

        return reviewMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getTaskReviews(Long taskId) {
        return reviewRepository.findByTaskIdOrderByIdDesc(taskId).stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getUserReviews(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı: " + username));
        return reviewRepository.findByReceiverIdOrderByIdDesc(user.getId()).stream()
                .map(reviewMapper::toResponse)
                .toList();
    }
}
