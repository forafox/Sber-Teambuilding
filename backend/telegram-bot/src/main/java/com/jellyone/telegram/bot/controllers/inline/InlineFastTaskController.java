package com.jellyone.telegram.bot.controllers.inline;

import com.jellyone.telegram.bot.api.TelegramApi;
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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.toIntExact;

@Component
@RequiredArgsConstructor
@Profile("telegram")
public class InlineFastTaskController implements InlineController {
    private final KeyboardService keyboardService;
    private final TaskSaveService taskSaveService;
    private final StateMachineService stateMachineService;
    private final TelegramMessageService messageService;
    private final TelegramApi telegramApi;

    @Override
    public List<BotApiMethod<?>> routeByState(long chatId, String userInput, int messageID) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        switch (stateMachineService.getUserStateMachine(chatId)) {
            case ANSWERING_ID_FAST -> result = parseTaskID(chatId, userInput, messageID);
        }
        return result;
    }

    private List<BotApiMethod<?>> parseTaskID(long chatId, String userInput, int messageID) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        result.add(new DeleteMessage(String.valueOf(chatId), toIntExact(messageID)));

        long eventID = Long.parseLong(userInput);
        String eventTitle = telegramApi.getEventTitleById(eventID);
        TaskDTO task = new TaskDTO(eventID, chatId);
        taskSaveService.save(chatId, task);

        result.add(messageService.createMessage(chatId, "Хорошо, введите название задачи для мероприятия *" + eventTitle + "*", keyboardService.cancelKeyboard()));

        stateMachineService.sendUserEvent(chatId, UserInput.ANSWERED_ID_CREATE_FAST);
        return result;
    }

}
