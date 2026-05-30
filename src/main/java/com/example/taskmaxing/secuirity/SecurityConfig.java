package com.example.taskmaxing.secuirity;

import com.example.taskmaxing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider; // Artıq bu ApplicationConfig-dən gələcək

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .csrf(AbstractHttpConfigurer::disable) // JWT üçün CSRF-i bağlayırıq
                .authorizeHttpRequests(auth -> auth
                        // 1. Həm register, həm də login qapısını hamı üçün tam açırıq!
                        .requestMatchers("/users/register", "/api/users/register", "/users/login", "/users/refresh", "/users/refresh-token").
                        permitAll()
                        .requestMatchers("/tasks/**").authenticated()
                        // 2. Qalan bütün sorğular mütləq token tələb edir!
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Session yoxdur, ancaq JWT
                )
                .authenticationProvider(authenticationProvider) // ApplicationConfig-dən gələn provayder
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Bizim filter

        return http.build();
    }}