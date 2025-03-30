package com.jellyone.telegram.bot;

import com.jellyone.telegram.bot.controllers.RegionRouter;
import com.jellyone.telegram.bot.services.CheckService;
import com.jellyone.telegram.bot.services.TelegramMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("telegram")
@Slf4j
public class SberBot implements SpringLongPollingBot, LongPollingUpdateConsumer {
    private final TelegramClient telegramClient = new OkHttpTelegramClient(getBotToken());

    private final RegionRouter regionRouter;
    private final CheckService checkService;
    private final TelegramMessageService messageService;

    @Override
    public String getBotToken() {
        return "7627331116:AAGPJhtw2RaHN86GL2fbdWTlrS7EEOWu1bA";
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    public void execute(BotApiMethod<?> message) {
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeList(List<BotApiMethod<?>> methods) {
        for (BotApiMethod<?> exec : methods) {
            execute(exec);
        }
    }

    @Override
    public void consume(List<Update> updates) {
        for (Update update : updates) {
            if (checkService.checkForRegistration(update)) {
                log.info("Check for registration found");
                executeList(regionRouter.getExecuteListByUpdate(update));
            } else {
                log.info("Not check for registration found");
                if (update.hasMessage()) {
                    execute(messageService.createMessage(
                            update.getMessage().getChatId(),
                            Constants.REGISTRATION_FAILED));
                } else if (update.hasCallbackQuery()) {
                    execute(messageService.createMessage(
                            update.getCallbackQuery().getMessage().getChatId(),
                            Constants.REGISTRATION_FAILED));
                }
                log.info("End Updates cycle");
            }
        }
    }
}