package com.jellyone.telegram.bot;


import com.jellyone.telegram.bot.api.Notificator;
import com.jellyone.telegram.bot.services.TelegramMessageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Profile("telegram")
public class TelegramNotificator implements Notificator {
    private final SberBot bot;
    private final TelegramMessageService messageService;

    @Override
    @Transactional
    public void notificatorNewEvent(long chatId, String eventTitle) {
        bot.execute(messageService.createMessage(chatId, "У вас новое мероприятие, *" + eventTitle + "*! Не забудьте о нем!"));
    }

    @Override
    @Transactional
    public void notificatorEventEnd(long chatId, String eventTitle) {
        bot.execute(messageService.createMessage(chatId, "Мероприятие *" + eventTitle + "* закончилось!"));
    }

    @Override
    @Transactional
    public void notificatorNewTask(long chatId, String taskTitle, String taskDescription) {
        bot.execute(messageService.createMessage(chatId, "Вам назначена задача *" + taskTitle + "*!" + System.lineSeparator() + "Её описание: " + taskDescription));
    }
}
