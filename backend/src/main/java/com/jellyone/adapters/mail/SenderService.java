package com.jellyone.adapters.mail;

public interface SenderService {
    void sendMail(String email, Long userId, Long eventId);
    void sendMail(Long userId, Long eventId);
}
