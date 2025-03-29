package com.jellyone.service;

import com.jellyone.domain.User;
import com.jellyone.web.security.JwtTokenProvider;
import com.jellyone.web.request.JwtRequest;
import com.jellyone.web.response.JwtResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtResponse login(JwtRequest loginRequest) {
        log.info("Try to login with username: {}", loginRequest.username());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );
        } catch (Exception e) {
            throw new BadCredentialsException(e.getMessage());
        }

        User user = userService.getByUsername(loginRequest.username());
        log.info("User with username: {} logged in", loginRequest.username());

        return new JwtResponse(
                user.getId(),
                user.getUsername(),
                jwtTokenProvider.createAccessToken(user.getId(), user.getUsername(), user.getRole()),
                jwtTokenProvider.createRefreshToken(user.getId(), user.getUsername())
        );
    }

    public JwtResponse refresh(String refreshToken) {
        log.info("Try to refresh token: {}", refreshToken);
        return jwtTokenProvider.refreshUserTokens(refreshToken);
    }
}
