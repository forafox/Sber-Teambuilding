package com.jellyone.telegram.bot;


import com.jellyone.telegram.bot.services.TelegramMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Profile("telegram")
public class Notificator {
    private final SberBot bot;
    private final TelegramMessageService messageService;

    public void notificatorEventStart(long chatId, String eventTitle) {
        bot.execute(messageService.createMessage(chatId, "У вас новое мероприятие, *" + eventTitle + "*! Не забудьте о нем!"));
    }
    public void notificatorEventEnd(long chatId, String eventTitle) {
        bot.execute(messageService.createMessage(chatId, "Мероприятие *" + eventTitle + "* закончилось!"));
    }
    public void notificatorNewTask(long chatId, String taskTitle, String taskDescription) {
        bot.execute(messageService.createMessage(chatId, "Вам назначена задача *" + taskTitle + "*! Её описание: " + taskDescription));
    }
}
