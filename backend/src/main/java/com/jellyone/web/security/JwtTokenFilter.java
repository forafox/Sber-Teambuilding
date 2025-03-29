package com.jellyone.web.security;

import com.jellyone.domain.enums.Role;
import com.jellyone.web.security.principal.AuthenticationFacade;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationFacade authenticationFacade;

    private static final List<String> ALLOWED_PATHS = Arrays.asList("docs", "swagger", "h2", "auth");

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String uri = httpRequest.getRequestURI();
        if (!uri.contains("/api")) {
            log.info("Request to static, passing it");
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (ALLOWED_PATHS.stream().anyMatch(uri::contains)) {
            log.info("Request to allowed path, passing it on without checking JWT");
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String bearerToken = httpRequest.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            sendUnauthorizedResponse(httpResponse, "No JWT token");
            return;
        }

        try {
            bearerToken = bearerToken.substring(7);
            if (jwtTokenProvider.validateToken(bearerToken)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(bearerToken);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            if (jwtTokenProvider.getAuthorities(bearerToken).contains(Role.ADMIN.name())) {
                authenticationFacade.setAdminRole();
            }
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException e) {
            sendUnauthorizedResponse(httpResponse, "Invalid JWT token");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(message);
    }
}
