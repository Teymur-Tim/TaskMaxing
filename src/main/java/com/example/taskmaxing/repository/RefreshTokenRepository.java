package com.example.taskmaxing.repository;

import com.example.taskmaxing.model.entity.RefreshToken;
import com.example.taskmaxing.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {


    Optional<RefreshToken> findByToken(String token);

    @Modifying
    void deleteByUser(User user);
}
