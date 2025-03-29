package com.jellyone.telegram.bot.stateMachine;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum State {
    MENU("Ожидается что-то из меню", Region.MENU),

    ANSWERING_ID_GET("Ожидается eventId", Region.GET_TASKS),

    ANSWERING_ID_CREATE("Ожидается eventId", Region.CREATE_TASK),
    CREATING_TASK_TITLE("Ожидается title", Region.CREATE_TASK),
    CREATING_TASK_DESCRIPTION("Ожидается description", Region.CREATE_TASK),
    CREATING_TASK_ASSIGNED("Ожидается id ответственного", Region.CREATE_TASK),
    CREATING_TASK_PRICE("Ожидается price", Region.CREATE_TASK),

    ANSWERING_ID_FAST("Ожидается eventId", Region.CREATE_FAST_TASK),
    FAST_TITLE("Ожидается title", Region.CREATE_FAST_TASK),
    FAST_PRICE("Ожидается price", Region.CREATE_FAST_TASK),

    ;

    private final String name;
    private final Region region;
}
