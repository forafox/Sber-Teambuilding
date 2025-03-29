package com.jellyone.telegram.bot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Service
@Profile("telegram")
@RequiredArgsConstructor
@Slf4j
public class TelegramMessageService {
    private final KeyboardService keyboardService;
    public SendMessage createMessage(long chatId, String text) {
        SendMessage msg = new SendMessage("" + chatId, text);
        msg.enableMarkdown(true);
        return msg;
    }

    public SendMessage createMessage(long chatId, String text, ReplyKeyboard keyboard) {
        SendMessage msg = createMessage(chatId, text);
        msg.setReplyMarkup(keyboard);
        return msg;
    }

    public SendMessage createMenu(long chatId) {
        SendMessage message = new SendMessage("" + chatId, "Меню");
        message.setReplyMarkup(keyboardService.menuKeyboard());
        return message;
    }

    public long getChatIdFromUpdate(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasEditedMessage()) {
            return update.getEditedMessage().getChatId();
        } else if (update.hasChannelPost()) {
            return update.getChannelPost().getChatId();
        } else if (update.hasEditedChannelPost()) {
            return update.getEditedChannelPost().getChatId();
        }
        log.info("Broken message from update: {}", update);
        return 0;
    }
}
