package com.example.taskmaxing.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import com.example.taskmaxing.model.enums.Role;
import org.hibernate.annotations.SoftDelete;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@SoftDelete
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private boolean phoneVisible = true;
    private String password;
    @Column(nullable = false)
    private boolean banned = false;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();
    private String bio;
    @Column(columnDefinition = "TEXT")
    private String avatar;
    @Column(name = "karma_points")
    private Long karmaPoints = 0L;
    @Column(name = "rating_sum")
    private Long ratingSum = 0L;
    @Column(name = "rating_count")
    private Long ratingCount = 0L;
    @OneToMany(mappedBy = "client", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !banned;
    }
}
