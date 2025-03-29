package com.jellyone.telegram.bot.services;

import com.jellyone.telegram.bot.stateMachine.State;
import com.jellyone.telegram.bot.stateMachine.StateMachine;
import com.jellyone.telegram.bot.stateMachine.UserInput;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("telegram")
public class StateMachineService {
    private final StateMachine stateMachine = new StateMachine();

    public State getUserStateMachine(long chatId) {
        return stateMachine.getUserState(chatId);
    }

    public void sendUserEvent(long chatId, UserInput event) {
        // юзер нажимает кнопку и создает тем самым какоето событие
        // это событие должно изменить состояние юзера
        // условно:
        // состояние "в меню"
        // нажал кнопку создать задачу и отправил событие "хочу создать задачу"
        // здесь мы это handl-им и изменяем состояние юзера на "заполняю title для задачи"
        stateMachine.sendEvent(chatId, event);
    }
}
