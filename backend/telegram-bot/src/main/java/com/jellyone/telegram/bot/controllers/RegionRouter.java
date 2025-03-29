package com.jellyone.telegram.bot.controllers;


import com.jellyone.telegram.bot.controllers.inline.InlineFastTaskController;
import com.jellyone.telegram.bot.controllers.inline.InlineGetController;
import com.jellyone.telegram.bot.controllers.inline.InlineTaskController;
import com.jellyone.telegram.bot.controllers.keyboard.FastTaskController;
import com.jellyone.telegram.bot.controllers.keyboard.MenuController;
import com.jellyone.telegram.bot.controllers.keyboard.TGTaskController;
import com.jellyone.telegram.bot.services.TelegramMessageService;
import com.jellyone.telegram.bot.services.StateMachineService;
import com.jellyone.telegram.bot.stateMachine.UserInput;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.ArrayList;
import java.util.List;

import static com.jellyone.telegram.bot.Constants.CANCEL;

@Component
@RequiredArgsConstructor
@Profile("telegram")
public class RegionRouter {
    private final StateMachineService stateMachineService;
    private final TelegramMessageService messageService;

    private final MenuController menuController;
    private final TGTaskController taskController;
    private final FastTaskController fastTaskController;
    private final InlineGetController inlineGetController;
    private final InlineTaskController inlineTaskController;
    private final InlineFastTaskController inlineFastTaskController;

    public List<BotApiMethod<?>> getExecuteListByUpdate(Update update) {
        List<BotApiMethod<?>> result = List.of(messageService.createMessage(
                messageService.getChatIdFromUpdate(update), "Не понимаю вас 🥲"));
        if (update.hasMessage() && update.getMessage().hasText()) {
            result = processUpdateMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            result = processUpdateCallbackQuery(update.getCallbackQuery());
        }
        return result;
    }

    private List<BotApiMethod<?>> processUpdateMessage(Message message) {
        long chatId = message.getChatId();
        String userInput = message.getText();
        if (userInput.equals("/start") || userInput.equals("/menu") || userInput.equals(CANCEL)) {
            stateMachineService.sendUserEvent(chatId, UserInput.MENU);
            return List.of(messageService.createMenu(chatId));
        }

        List<BotApiMethod<?>> executable = new ArrayList<>();
        switch (stateMachineService.getUserStateMachine(chatId).getRegion()) {
            case MENU -> executable = menuController.routeByState(chatId, userInput);
            case CREATE_TASK -> executable = taskController.routeByState(chatId, userInput);
            case CREATE_FAST_TASK -> executable = fastTaskController.routeByState(chatId, userInput);
        }
        return executable;
    }

    private List<BotApiMethod<?>> processUpdateCallbackQuery(CallbackQuery callback) {
        String callData = callback.getData();
        long chatId = callback.getMessage().getChatId();
        int messageId = callback.getMessage().getMessageId();
        if (callData.equalsIgnoreCase(CANCEL)) {
            stateMachineService.sendUserEvent(chatId, UserInput.MENU);
            return List.of(messageService.createMenu(chatId));
        }

        List<BotApiMethod<?>> executable = new ArrayList<>();
        switch (stateMachineService.getUserStateMachine(chatId).getRegion()) {
            case GET_TASKS -> executable = inlineGetController.routeByState(chatId, callData, messageId);
            case CREATE_TASK -> executable = inlineTaskController.routeByState(chatId, callData, messageId);
            case CREATE_FAST_TASK -> executable = inlineFastTaskController.routeByState(chatId, callData, messageId);
        }
        return executable;
    }
}
