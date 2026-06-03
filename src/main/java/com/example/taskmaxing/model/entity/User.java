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
// Soft delete: user silinəndə bazadan getmir, "deleted" sütunu true olur.
// Bütün sorğulara avtomatik "WHERE deleted = false" əlavə olunur (login, findByUsername və s.).
@SoftDelete
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    // Telefon nömrəsi: əlaqə üçün məcburidir və unikaldır —
    // bir nömrə ilə yalnız bir hesab açıla bilər (saxta/dublikat hesabların qarşısını alır).
    @Column(unique = true, nullable = false)
    private String phoneNumber;
    // Telefon nömrəsinin ictimai profildə görünüb-görünməməsi (default: görünür).
    // İstifadəçi məxfilik üçün bunu söndürə bilər — onda başqaları nömrəni görmür.
    @Column(nullable = false)
    private boolean phoneVisible = true;
    private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();
    private String bio;
    // Profil şəkli: kiçildilmiş base64 data URL kimi saxlanılır (xarici fayl anbarı yoxdur).
    // TEXT sütunu — uzun mətn tutur (Postgres-də @Lob/OID problemlərindən qaçmaq üçün).
    @Column(columnDefinition = "TEXT")
    private String avatar;
    @Column(name = "karma_points")
    private Long karmaPoints = 0L;
    // Reytinq aqreqatı: orta bal = ratingSum / ratingCount (rəy gələndə yenilənir).
    @Column(name = "rating_sum")
    private Long ratingSum = 0L;
    @Column(name = "rating_count")
    private Long ratingCount = 0L;
    // CascadeType.ALL deyil: user soft-delete olunanda onun task-ları SİLİNMƏSİN, tarixçə qalsın.
    @OneToMany(mappedBy = "client", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();
//----------------------------------------------------
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Bizim Enum rollarımızı Spring-in başa düşəcəyi "SimpleGrantedAuthority" obyektlərinə çeviririk
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Hesabın vaxtı keçməyib
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Hesab bloklanmayıb
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Şifrənin vaxtı keçməyib
    }

    @Override
    public boolean isEnabled() {
        return true; // Hesab aktivdir
    }
}
