package com.jellyone.telegram.bot.controllers.keyboard;

import com.jellyone.telegram.bot.Constants;
import com.jellyone.telegram.bot.api.TelegramApi;
import com.jellyone.telegram.bot.dto.EventDTO;
import com.jellyone.telegram.bot.dto.TaskDTO;
import com.jellyone.telegram.bot.services.KeyboardService;
import com.jellyone.telegram.bot.services.PrintableConverter;
import com.jellyone.telegram.bot.services.StateMachineService;
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
public class MenuController implements Controller {
    private final KeyboardService keyboardService;
    private final TelegramMessageService messageService;
    private final PrintableConverter printableConverter;
    private final StateMachineService stateMachineService;
    private final TelegramApi telegramApi;

    @Override
    public List<BotApiMethod<?>> routeByState(long chatId, String userInput) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        switch (userInput) {
            case Constants.MENU_BTN_MY_EVENTS -> result = myEvents(chatId);
            case Constants.MENU_BTN_MY_TASKS -> result = myTasks(chatId);
            case Constants.MENU_BTN_EVENT_TASKS -> result = myEventTasks(chatId);
            case Constants.MENU_BTN_CREATE_TASK -> result = createTask(chatId);
            case Constants.MENU_BTN_CREATE_FAST_TASK -> result = createFastTask(chatId);
        }
        return result;
    }

    private List<BotApiMethod<?>> myEvents(long chatId) {
        List<BotApiMethod<?>> result = new ArrayList<>();

        List<EventDTO> myEvents = telegramApi.getMyEvents(chatId);
        if (myEvents.isEmpty()) {
            result.add(messageService.createMessage(chatId, Constants.NO_ACTIVE_EVENTS, keyboardService.menuKeyboard()));
            return result;
        }

        String userEvents = "Ваши мероприятия: " + System.lineSeparator();
        userEvents += printableConverter.listToString(myEvents);

        result.add(messageService.createMessage(chatId, userEvents, keyboardService.menuKeyboard()));

        stateMachineService.sendUserEvent(chatId, UserInput.MENU);
        return result;
    }

    private List<BotApiMethod<?>> myTasks(long chatId) {
        List<BotApiMethod<?>> result = new ArrayList<>();

        List<TaskDTO> myTasks = telegramApi.getMyTasks(chatId);
        if (myTasks.isEmpty()) {
            result.add(messageService.createMessage(chatId, Constants.NO_ACTIVE_TASKS, keyboardService.menuKeyboard()));
            return result;
        }

        String usersTasks = "Ваши задачи по всем мероприятиям: " + System.lineSeparator();
        usersTasks += printableConverter.listToString(myTasks);

        result.add(messageService.createMessage(chatId, usersTasks, keyboardService.menuKeyboard()));

        stateMachineService.sendUserEvent(chatId, UserInput.MENU);
        return result;
    }

    private List<BotApiMethod<?>> chooseEvent(long chatId, String info, List<EventDTO> myEvents) {
        List<BotApiMethod<?>> result = new ArrayList<>();

        if (myEvents.isEmpty()) {
            result.add(messageService.createMessage(chatId, Constants.NO_ACTIVE_EVENTS, keyboardService.menuKeyboard()));
            return result;
        }

        String userEvents = "Ваши мероприятия: " + System.lineSeparator();
        userEvents += printableConverter.listToString(myEvents);
        userEvents += info;

        SendMessage msg = messageService.createMessage(chatId, userEvents);
        String[] ids = printableConverter.listIDFromPrintable(myEvents);
        String[] titles = printableConverter.listTitleFromPrintable(myEvents);
        msg.setReplyMarkup(keyboardService.inlineListKeyboard(titles, ids, 2));

        result.add(msg);
        return result;
    }

    private List<BotApiMethod<?>> myEventTasks(long chatId) {
        List<EventDTO> myEvents = telegramApi.getMyEvents(chatId);
        List<BotApiMethod<?>> result = chooseEvent(chatId, Constants.CHOOSE_EVENT_ID, myEvents);

        if (!myEvents.isEmpty()) {
            stateMachineService.sendUserEvent(chatId, UserInput.ANSWERING_ID_GET);
        }
        return result;
    }

    private List<BotApiMethod<?>> createTask(long chatId) {
        List<EventDTO> myEvents = telegramApi.getMyEvents(chatId);
        List<BotApiMethod<?>> result = chooseEvent(chatId, Constants.CHOOSE_EVENT_ID_CREATE, myEvents);

        if (!myEvents.isEmpty()) {
            stateMachineService.sendUserEvent(chatId, UserInput.CREATE_TASK);
        }
        return result;
    }

    private List<BotApiMethod<?>> createFastTask(long chatId) {
        List<EventDTO> myEvents = telegramApi.getMyEvents(chatId);
        List<BotApiMethod<?>> result = chooseEvent(chatId, Constants.CHOOSE_EVENT_ID_FAST_CREATE, myEvents);

        if (!myEvents.isEmpty()) {
            stateMachineService.sendUserEvent(chatId, UserInput.FAST_TASK);
        }
        return result;
    }
}
