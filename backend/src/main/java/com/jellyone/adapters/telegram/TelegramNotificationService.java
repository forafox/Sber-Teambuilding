package com.jellyone.adapters.telegram;

import com.jellyone.domain.TelegramUser;
import com.jellyone.exception.ResourceNotFoundException;
import com.jellyone.service.TelegramUserService;
import com.jellyone.service.UserService;
import com.jellyone.telegram.bot.api.Notificator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramNotificationService {
    private final Notificator notificator;
    private final TelegramUserService telegramUserService;
    private final UserService userService;

    public void sendTaskNotification(String username, String title, String description) {
        long chatId = getChatIdByUsername(username);
        if (chatId != -1) {
            log.info("Sending notification about new task via telegram to chat with id: {}", chatId);
            notificator.notificatorNewTask(chatId, title, description);
        }
    }

    public void sendNewEventNotification(Long participantId, String title) {
        long chatId = getChatIdByUsername(userService.getById(participantId).getUsername());
        if (chatId != -1) {
            log.info("Sending notification about new event via telegram to chat with id: {}", chatId);
            notificator.notificatorNewEvent(chatId, title);
        }
    }

    public void sendEventEndNotification(Long participantId, String title) {
        long chatId = getChatIdByUsername(userService.getById(participantId).getUsername());
        if (chatId != -1) {
            log.info("Sending notification about event ended via telegram to chat with id: {}", chatId);
            notificator.notificatorEventEnd(chatId, title);
        }
    }

    private long getChatIdByUsername(String username) {
        TelegramUser telegramUser;
        try {
            telegramUser = telegramUserService.getByUsername(username);
        } catch (ResourceNotFoundException e) {
            log.info("User not found: {}", username);
            return -1;
        }
        if (telegramUser.getTelegramChatId() != null) {
            return telegramUser.getTelegramChatId();
        }
        return -1;
    }
}
