package com.jellyone.telegram.bot.controllers.keyboard;

import com.jellyone.telegram.bot.api.TelegramApi;
import com.jellyone.telegram.bot.dto.TaskDTO;
import com.jellyone.telegram.bot.services.StateMachineService;
import com.jellyone.telegram.bot.services.TaskSaveService;
import com.jellyone.telegram.bot.services.TelegramMessageService;
import com.jellyone.telegram.bot.stateMachine.UserInput;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("telegram")
public class FastTaskController implements Controller {
    private final  TaskSaveService taskSaveService;
    private final  StateMachineService stateMachineService;
    private final  TelegramMessageService messageService;
    private final TelegramApi telegramApi;

    @Override
    public List<BotApiMethod<?>> routeByState(long chatId, String userInput) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        switch (stateMachineService.getUserStateMachine(chatId)) {
            case FAST_TITLE -> result = createTaskTitle(chatId, userInput);
            case FAST_PRICE -> result = createTaskPrice(chatId, userInput);
        }
        return result;
    }

    private List<BotApiMethod<?>> createTaskTitle(long chatId, String userInput) {
        List<BotApiMethod<?>> result = new ArrayList<>();

        TaskDTO task = taskSaveService.get(chatId);
        task.setTitle(userInput);
        taskSaveService.save(chatId, task);

        result.add(messageService.createMessage(chatId, "Отлично, введите потраченную сумму"));

        stateMachineService.sendUserEvent(chatId, UserInput.CREATE_FAST_TITLE);
        return result;
    }

    private List<BotApiMethod<?>> createTaskPrice(long chatId, String userInput) {
        List<BotApiMethod<?>> result = new ArrayList<>();

        TaskDTO task = taskSaveService.get(chatId);
        try {
            task.setExpenses(Double.parseDouble(userInput));
        } catch (NumberFormatException e) {
            result.add(messageService.createMessage(chatId, "Введите число плииз"));
            return result;
        }
        telegramApi.saveTask(task);
        taskSaveService.remove(task.getEventID());

        result.add(messageService.createMessage(chatId, "Задача создана!"));
        result.add(messageService.createMenu(chatId));

        stateMachineService.sendUserEvent(chatId, UserInput.CREATE_FAST_PRICE);
        return result;
    }
}
