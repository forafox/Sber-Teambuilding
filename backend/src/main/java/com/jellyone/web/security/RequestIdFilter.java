package com.jellyone.web.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Component
@Slf4j
public class RequestIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            MDC.put("RID", generateShortRequestId());
            MDC.put("UID", generateUidFromUsername());

            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error in RequestIdFilter", e);
        } finally {
            MDC.clear();
        }
    }

    private String generateShortRequestId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String generateUidFromUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication != null ? authentication.getName() : null;
        return (username != null) ? generateUid(username).substring(0, 8) : null;
    }

    private String generateUid(String username) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(username.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not found", e);
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
