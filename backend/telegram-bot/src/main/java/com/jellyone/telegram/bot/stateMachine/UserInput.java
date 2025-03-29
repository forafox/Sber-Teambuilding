package com.jellyone.telegram.bot.stateMachine;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserInput {
    START("start"),
    MENU("Юзер хочет в меню"),

    ANSWERING_ID_GET("Получить Мои задачи в меню -> ожидаем eventId"),
    ANSWERED_ID_GET("Получен eventId -> отобразить список задач -> в меню"),

    CREATE_TASK("Создать задачу -> ожидаем eventId"),
    ANSWERED_ID_CREATE("Получен eventId -> ожидаем title"),
    CREATE_TASK_TITLE("Получен title -> ожидаем description"),
    CREATE_TASK_DESCRIPTION("Получен description -> ожидаем id ответственного"),
    CREATE_TASK_ASSIGNED("Получен id ответственного -> ожидаем price"),
    CREATE_TASK_PRICE("Получен price -> создать задачу -> в меню"),

    FAST_TASK("Создать быструю задачу -> ожидаем eventId"),
    ANSWERED_ID_CREATE_FAST("Получен eventId -> ожидаем title"),
    CREATE_FAST_TITLE("Получен title -> ожидаем price"),
    CREATE_FAST_PRICE("Получен price -> создать задачу -> в меню"),

    ;

    private final String name;
}
