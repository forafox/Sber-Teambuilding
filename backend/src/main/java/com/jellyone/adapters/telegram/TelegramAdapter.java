package com.jellyone.adapters.telegram;

import com.jellyone.domain.Event;
import com.jellyone.domain.Task;
import com.jellyone.domain.User;
import com.jellyone.domain.enums.TaskStatus;
import com.jellyone.service.EventService;
import com.jellyone.service.TaskService;
import com.jellyone.service.TelegramUserService;
import com.jellyone.service.UserService;
import com.jellyone.telegram.bot.api.TelegramApi;
import com.jellyone.telegram.bot.dto.EventDTO;
import com.jellyone.telegram.bot.dto.ParticipantDTO;
import com.jellyone.telegram.bot.dto.TaskDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RequiredArgsConstructor
@Service
@Profile("telegram")
public class TelegramAdapter implements TelegramApi {
    private final EventService eventService;
    private final TaskService taskService;
    private final TelegramUserService telegramUserService;
    private final UserService userService;

    private final ParticipantToParticipantDTO participantMap;
    private final EventToEventDTO eventMap;
    private final TelegramTaskToTaskDTO taskMap;


    @Override
    @Transactional
    public List<EventDTO> getMyEvents(long chatId) {
        String username = telegramUserService.getByTelegramChatId(chatId).getUser().getUsername();
        Page<Event> page = eventService.getAll(username, 0, 30);
        return page.get()
                .map(eventMap::eventToEventDTO)
                .toList();
    }

    @Override
    @Transactional
    public List<TaskDTO> getMyTasks(long chatId) {
        String username = telegramUserService.getByTelegramChatId(chatId).getUser().getUsername();
        List<Long> idList = eventService.getAll(username, 0, 30).get().map(Event::getId).toList();
        List<TaskDTO> taskDTOList = new ArrayList<>();
        for (Long id : idList) {
            Page<Task> page = taskService.getAll(0, 30, id);
            taskDTOList.addAll(page.get()
                    .map(taskMap::taskToTaskDTO)
                    .toList());
        }
        return taskDTOList;
    }

    @Override
    @Transactional
    public List<TaskDTO> getMyEventTasks(long chatId, long eventId) {
        long userId = telegramUserService.getByTelegramChatId(chatId).getUser().getId();
        Page<Task> page = taskService.getAll(0, 30, eventId);
        return page.get()
                .map(taskMap::taskToTaskDTO)
                .filter(taskDTO -> taskDTO.getAssignedID() == userId)
                .toList();
    }

    @Override
    @Transactional
    public String getEventTitleById(long eventId) {
        Event event = eventService.getById(eventId);
        return event.getTitle();
    }

    @Override
    @Transactional
    public List<ParticipantDTO> getParticipants(long eventId) {
        List<User> part = eventService.getById(eventId).getParticipants();
        System.out.println(Arrays.toString(part.stream().map(User::getUsername).toArray()));
        return part.stream()
                .map(participantMap::participantToParticipantDTO)
                .toList();
    }

    @Override
    @Transactional
    public void saveTask(TaskDTO taskDTO) {
        String title = taskDTO.getTitle();

        String assigneeUsername;
        String authorUsername = telegramUserService.getByTelegramChatId(taskDTO.getAuthorID()).getUser().getUsername();
        if (taskDTO.getAssignedID() >= 1) {
            assigneeUsername = userService.getById(taskDTO.getAssignedID()).getUsername();
        } else {
            assigneeUsername = authorUsername;
        }

        TaskStatus status = TaskStatus.IN_PROGRESS;
        String description = taskDTO.getDescription();
        Double expenses = taskDTO.getExpenses();
        Long eventId = taskDTO.getEventID();
        // TODO: read URL from Telegram?
        String url = null;

        taskService.create(title, assigneeUsername, authorUsername, status, description, expenses, eventId, url);
    }

    @Override
    public boolean telegramUsernameExists(String username) {
        return telegramUserService.telegramUsernameExists(username);
    }

    @Override
    public void saveChatIdByUsername(String username, long chatId) {
        telegramUserService.updateChatIdByTelegramUsername(username, chatId);
    }
}
