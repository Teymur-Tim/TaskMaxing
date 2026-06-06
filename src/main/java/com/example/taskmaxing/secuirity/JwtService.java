package com.example.taskmaxing.secuirity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Secret artıq kodda hardcode deyil — application.yaml / JWT_SECRET env dəyişənindən gəlir.
    @Value("${jwt.secret}")
    private String secretKey;

    // Access token lives 15 minutes; refresh token lives 7 days and is stored in the DB.
    private static final long ACCESS_TOKEN_EXPIRY_MS = 1000L * 60 * 15;

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Rolları token-ə yazırıq ki, frontend admin panelini göstərib-göstərməməyi
        // dərhal (əlavə sorğusuz) bilsin. "ROLE_" prefiksini atırıq: ["CLIENT","ADMIN"].
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.replaceFirst("^ROLE_", ""))
                .toList());
        return generateAccessToken(claims, userDetails);
    }

    public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY_MS))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        // Secret-i birbaşa UTF-8 bayt kimi götürürük (Base64 tələb olunmur, ona görə
        // istənilən mətn olar). HS256 üçün açar ən azı 32 bayt (256 bit) olmalıdır,
        // yəni JWT_SECRET ən azı 32 simvol uzunluğunda olmalıdır.
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
