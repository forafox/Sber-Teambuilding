package com.jellyone.telegram.bot.stateMachine;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Region {
    MENU("Меню"),
    GET_TASKS("Ожидаем eventId"),
    CREATE_TASK("Создать задачу"),
    CREATE_FAST_TASK("Создать быструю задачу");

    private final String name;
}
