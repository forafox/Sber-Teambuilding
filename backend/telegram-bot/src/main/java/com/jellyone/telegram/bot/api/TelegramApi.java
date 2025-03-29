package com.jellyone.telegram.bot.api;

import com.jellyone.telegram.bot.dto.EventDTO;
import com.jellyone.telegram.bot.dto.ParticipantDTO;
import com.jellyone.telegram.bot.dto.TaskDTO;

import java.util.List;

public interface TelegramApi {
    List<EventDTO> getMyEvents(long chatId);
    List<TaskDTO> getMyTasks(long chatId);
    List<TaskDTO> getMyEventTasks(long chatId, long eventId);

    String getEventTitleById(long eventId);

    List<ParticipantDTO> getParticipants(long eventId);

    void saveTask(TaskDTO taskDTO);

    boolean telegramUsernameExists(String username);
    void saveChatIdByUsername(String username, long chatId);

}
