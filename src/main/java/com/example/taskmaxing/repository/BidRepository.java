package com.example.taskmaxing.repository;

import com.example.taskmaxing.model.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
//istifade olunmurr!!!
@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByTaskId(Long taskId);
}