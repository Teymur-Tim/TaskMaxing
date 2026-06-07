package com.example.taskmaxing.repository;

import com.example.taskmaxing.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByTaskIdAndReviewerId(Long taskId, Long reviewerId);

    List<Review> findByTaskIdOrderByIdDesc(Long taskId);

    List<Review> findByReceiverIdOrderByIdDesc(Long receiverId);
}
