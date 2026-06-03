package com.example.taskmaxing.repository;

import com.example.taskmaxing.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Bu istifadəçi bu tapşırıq üçün artıq rəy yazıbmı? (təkrar rəyin qarşısını alır)
    boolean existsByTaskIdAndReviewerId(Long taskId, Long reviewerId);

    // Bir tapşırığın bütün rəyləri (hər iki tərəf), ən yenidən köhnəyə
    List<Review> findByTaskIdOrderByIdDesc(Long taskId);

    // Bir istifadəçi haqqında yazılmış bütün rəylər, ən yenidən köhnəyə
    List<Review> findByReceiverIdOrderByIdDesc(Long receiverId);
}
