package com.jellyone.adapters.mail;

public interface SenderService {
    void sendMail(String email, Long userId, Long eventId);
    void sendMail(Long userId, Long eventId);
    void sendAttachedMail(String email, Long userId, Long eventId);
    void sendAttachedMail(Long userId, Long eventId);
}
