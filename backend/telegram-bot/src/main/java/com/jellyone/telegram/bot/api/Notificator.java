package com.jellyone.telegram.bot.api;

public interface Notificator {
    void notificatorNewEvent(long chatId, String eventTitle);
    void notificatorEventEnd(long chatId, String eventTitle);
    void notificatorNewTask(long chatId, String taskTitle, String taskDescription);
}
