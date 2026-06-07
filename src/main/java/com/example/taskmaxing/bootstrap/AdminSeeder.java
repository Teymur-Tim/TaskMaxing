package com.example.taskmaxing.bootstrap;

import com.example.taskmaxing.model.entity.User;
import com.example.taskmaxing.model.enums.Role;
import com.example.taskmaxing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    @Value("${app.admin-username:}")
    private String adminUsername;

    @Override
    @Transactional
    public void run(String... args) {
        if (adminUsername == null || adminUsername.isBlank()) {
            log.info("app.admin-username təyin olunmayıb — admin seed atlanır.");
            return;
        }
        userRepository.findByUsername(adminUsername).ifPresentOrElse(
                user -> {
                    if (user.getRoles().add(Role.ADMIN)) {
                        userRepository.save(user);
                        log.info("'{}' istifadəçisinə ADMIN rolu verildi.", adminUsername);
                    } else {
                        log.info("'{}' artıq ADMIN-dir.", adminUsername);
                    }
                },
                () -> log.warn("Admin user '{}' tapılmadı — əvvəlcə qeydiyyatdan keç, sonra restart et.", adminUsername)
        );
    }
}
