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

    // Token mətni ilə bazadan axtarmaq üçün (Refresh istəyəndə lazım olacaq)
    Optional<RefreshToken> findByToken(String token);

    // İstifadəçi hər dəfə yeni login edəndə və ya logout olanda köhnə refresh tokenini silmək üçün
    @Modifying
    void deleteByUser(User user);
}
