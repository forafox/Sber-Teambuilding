package com.jellyone.telegram.bot.controllers.inline;

import com.jellyone.telegram.bot.api.TelegramApi;
import com.jellyone.telegram.bot.dto.TaskDTO;
import com.jellyone.telegram.bot.services.PrintableConverter;
import com.jellyone.telegram.bot.services.StateMachineService;
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
public class InlineGetController implements InlineController {
    private final  PrintableConverter printableConverter;
    private final  TelegramMessageService messageService;
    private final  StateMachineService stateMachineService;
    private final TelegramApi telegramApi;

    @Override
    public List<BotApiMethod<?>> routeByState(long chatId, String userInput, int messageID) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        result.add(new DeleteMessage(String.valueOf(chatId), toIntExact(messageID)));

        long eventID = Long.parseLong(userInput);
        String enteredEventTitle = telegramApi.getEventTitleById(eventID);
        List<TaskDTO> myTasks = telegramApi.getMyEventTasks(chatId, eventID);

        String botText = "Ваши задачи на мероприятие *" + enteredEventTitle + "*:" + System.lineSeparator();
        botText += printableConverter.listToString(myTasks);
        botText = myTasks.isEmpty() ? "У вас нет активных задач" : botText;

        result.add(messageService.createMessage(chatId, botText));
        result.add(messageService.createMenu(chatId));

        stateMachineService.sendUserEvent(chatId, UserInput.ANSWERED_ID_GET);
        return result;
    }
}