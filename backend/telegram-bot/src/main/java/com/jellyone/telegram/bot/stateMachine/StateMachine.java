package com.jellyone.telegram.bot.stateMachine;

import java.util.HashMap;
import java.util.Map;

import static com.jellyone.telegram.bot.stateMachine.UserInput.*;

public class StateMachine {
    private final Map<Long, State> userStates = new HashMap<>();
    private final Map<UserInput, State> stateTransition = new HashMap<>();

    public StateMachine() {
        stateTransition.put(START, State.MENU);
        stateTransition.put(MENU, State.MENU);

        stateTransition.put(ANSWERING_ID_GET, State.ANSWERING_ID_GET);
        stateTransition.put(ANSWERED_ID_GET, State.MENU);

        stateTransition.put(CREATE_TASK, State.ANSWERING_ID_CREATE);
        stateTransition.put(ANSWERED_ID_CREATE, State.CREATING_TASK_TITLE);
        stateTransition.put(CREATE_TASK_TITLE, State.CREATING_TASK_DESCRIPTION);
        stateTransition.put(CREATE_TASK_DESCRIPTION, State.CREATING_TASK_ASSIGNED);
        stateTransition.put(CREATE_TASK_ASSIGNED, State.CREATING_TASK_PRICE);
        stateTransition.put(CREATE_TASK_PRICE, State.MENU);

        stateTransition.put(FAST_TASK, State.ANSWERING_ID_FAST);
        stateTransition.put(ANSWERED_ID_CREATE_FAST, State.FAST_TITLE);
        stateTransition.put(CREATE_FAST_TITLE, State.FAST_PRICE);
        stateTransition.put(CREATE_FAST_PRICE, State.MENU);
        assert stateTransition.size() == UserInput.values().length;
    }

    public void sendEvent(long chatId, UserInput event) {
        userStates.put(chatId, stateTransition.get(event));
    }

    public State getUserState(long chatId) {
        return userStates.getOrDefault(chatId, State.MENU);
    }
}
