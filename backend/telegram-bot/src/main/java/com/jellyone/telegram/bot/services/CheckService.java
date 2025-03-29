package com.jellyone.telegram.bot.services;

import com.jellyone.telegram.bot.api.TelegramApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Service
@Profile("telegram")
@RequiredArgsConstructor
@Slf4j
public class CheckService {
    // TODO вместо ArrayList следовало бы использовать key–value бд -- Redis
    private final List<String> registered = new ArrayList<>();
    private final TelegramApi telegramApi;

    public boolean checkForRegistration(Update update) {
        boolean result = false;
        if (update.hasMessage()) {
            log.info("Checking for registration");
            String username = update.getMessage().getFrom().getUserName();
            long chatId = update.getMessage().getChatId();
            result = checkForRegistration(username, chatId);
        } else if (update.hasCallbackQuery()) {
            log.info("Checking for callback query");
            String username = update.getCallbackQuery().getFrom().getUserName();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            result = checkForRegistration(username, chatId);
        }
        return result;
    }

    public boolean checkForRegistration(String username, long chatId) {
        if (registered.contains(username)) {
            return true;
        }
        if (telegramApi.telegramUsernameExists(username)) {
            registered.add(username);
            telegramApi.saveChatIdByUsername(username, chatId);
            return true;
        }
        return false;
    }
}
