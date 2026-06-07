package com.example.taskmaxing.controller;

import com.example.taskmaxing.model.dto.request.CreateReviewRequest;
import com.example.taskmaxing.model.dto.response.ReviewResponse;
import com.example.taskmaxing.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/tasks/{taskId}/reviews")
    public ResponseEntity<ReviewResponse> create(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateReviewRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(reviewService.createReview(taskId, authentication.getName(), request));
    }

    @GetMapping("/tasks/{taskId}/reviews")
    public ResponseEntity<List<ReviewResponse>> taskReviews(@PathVariable Long taskId) {
        return ResponseEntity.ok(reviewService.getTaskReviews(taskId));
    }

    @GetMapping("/users/{username}/reviews")
    public ResponseEntity<List<ReviewResponse>> userReviews(@PathVariable String username) {
        return ResponseEntity.ok(reviewService.getUserReviews(username));
    }
}
