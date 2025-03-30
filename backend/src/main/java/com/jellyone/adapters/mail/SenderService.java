package com.jellyone.adapters.mail;

import com.jellyone.mail.dto.TaskDTO;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.List;

public interface SenderService {
    void sendMail(String email, Long userId, Long eventId);
    void sendMail(Long userId, Long eventId);
    void sendAttachedMail(String email, Long userId, Long eventId);
    void sendAttachedMail(Long userId, Long eventId);
}
