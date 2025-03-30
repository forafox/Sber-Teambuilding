package com.jellyone.adapters.telegram;

import com.jellyone.telegram.bot.api.Notificator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("!telegram")
public class NoOpNotificator implements Notificator {

    @Override
    public void notificatorNewEvent(long chatId, String eventTitle) {
        log.debug("Telegram module not set: notification about event start {} not send", eventTitle);
    }

    @Override
    public void notificatorEventEnd(long chatId, String eventTitle) {
        log.debug("Telegram module not set: notification about event end {} not send", eventTitle);
    }

    @Override
    public void notificatorNewTask(long chatId, String taskTitle, String taskDescription) {
        log.debug("Telegram module not set: notification about create task {} not send", taskTitle);
    }
} 