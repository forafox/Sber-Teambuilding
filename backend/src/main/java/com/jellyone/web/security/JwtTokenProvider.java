package com.jellyone.web.security;

import com.jellyone.domain.User;
import com.jellyone.domain.enums.Role;
import com.jellyone.service.UserService;
import com.jellyone.service.props.JwtProperties;
import com.jellyone.web.response.JwtResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    private SecretKey key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(Optional.ofNullable(jwtProperties.getSecret())
                .map(String::getBytes)
                .orElse(new byte[32]));
    }

    public String createAccessToken(Long userId, String username, Role role) {
        Claims claims = Jwts.claims()
                .setSubject(username)
                .add("id", userId)
                .add("roles", resolveRoles(Set.of(role)))
                .build();

        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtProperties.getAccess());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }

    private List<String> resolveRoles(Set<Role> roles) {
        return roles.stream().map(Enum::name).collect(Collectors.toList());
    }

    public String createRefreshToken(Long userId, String username) {
        Claims claims = Jwts.claims()
                .setSubject(username)
                .add("id", userId)
                .build();

        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtProperties.getRefresh());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }

    public JwtResponse refreshUserTokens(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        Long userId = Long.parseLong(getId(refreshToken));
        User user = userService.getById(userId);

        return new JwtResponse(
                userId,
                user.getUsername(),
                createAccessToken(userId, user.getUsername(), user.getRole()),
                createRefreshToken(userId, user.getUsername())
        );
    }

    public boolean validateToken(String token) {
        Jws<Claims> claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
        return !claims.getPayload().getExpiration().before(new Date());
    }

    private String getId(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id").toString();
    }

    private String getUserName(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public List<String> getAuthorities(String token) {
        String rolesString = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("roles")
                .toString();

        return Arrays.stream(rolesString.replace("[", "").replace("]", "").split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public Authentication getAuthentication(String token) {
        String username = getUserName(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
