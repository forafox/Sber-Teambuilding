package com.jellyone.telegram.bot.controllers.keyboard;

import com.jellyone.telegram.bot.api.TelegramApi;
import com.jellyone.telegram.bot.dto.ParticipantDTO;
import com.jellyone.telegram.bot.dto.TaskDTO;
import com.jellyone.telegram.bot.services.KeyboardService;
import com.jellyone.telegram.bot.services.StateMachineService;
import com.jellyone.telegram.bot.services.TaskSaveService;
import com.jellyone.telegram.bot.services.TelegramMessageService;
import com.jellyone.telegram.bot.stateMachine.UserInput;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("telegram")
public class TGTaskController implements Controller {
    private final StateMachineService stateMachineService;
    private final TaskSaveService taskSaveService;
    private final TelegramMessageService messageService;
    private final KeyboardService keyboardService;
    private final TelegramApi telegramApi;

    @Override
    public List<BotApiMethod<?>> routeByState(long chatId, String userInput) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        switch (stateMachineService.getUserStateMachine(chatId)) {
            case CREATING_TASK_TITLE -> result = createTaskTitle(chatId, userInput);
            case CREATING_TASK_DESCRIPTION -> result = createTaskDescription(chatId, userInput);
            case CREATING_TASK_PRICE -> result = createTaskPrice(chatId, userInput);
        }
        return result;
    }

    private List<BotApiMethod<?>> createTaskTitle(long chatId, String userInput) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        TaskDTO task = taskSaveService.get(chatId);
        task.setTitle(userInput);
        taskSaveService.save(chatId, task);

        result.add(messageService.createMessage(chatId, "Отлично, введите описание задачи", keyboardService.cancelKeyboard()));

        stateMachineService.sendUserEvent(chatId, UserInput.CREATE_TASK_TITLE);
        return result;
    }

    private List<BotApiMethod<?>> createTaskDescription(long chatId, String userInput) {
        List<BotApiMethod<?>> result = new ArrayList<>();

        TaskDTO task = taskSaveService.get(chatId);
        task.setDescription(userInput);
        taskSaveService.save(chatId, task);

        SendMessage msg = messageService.createMessage(chatId, "Превосходно, введите имя ответственного");
        List<ParticipantDTO> participants = telegramApi.getParticipants(task.getEventID());
        String[] participantsNames = new String[participants.size()];
        String[] participantsIDs = new String[participants.size()];

        for (int i = 0; i < participants.size(); i++) {
            participantsNames[i] = participants.get(i).getName();
            participantsIDs[i] = String.valueOf(participants.get(i).getId());
        }

        msg.setReplyMarkup(keyboardService.inlineListKeyboard(participantsNames, participantsIDs, 2));
        result.add(msg);

        stateMachineService.sendUserEvent(chatId, UserInput.CREATE_TASK_DESCRIPTION);
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

        stateMachineService.sendUserEvent(chatId, UserInput.CREATE_TASK_PRICE);
        return result;
    }
}
