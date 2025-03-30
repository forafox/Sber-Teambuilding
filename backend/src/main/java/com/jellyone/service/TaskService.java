package com.jellyone.service;

import com.jellyone.domain.Event;
import com.jellyone.domain.Task;
import com.jellyone.domain.User;
import com.jellyone.domain.enums.ServerChange;
import com.jellyone.domain.enums.TaskStatus;
import com.jellyone.exception.ResourceNotFoundException;
import com.jellyone.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final EventService eventService;
    private final WebSocketSessionService webSocketSessionService;

    public Task create(
            String title,
            String assigneeUsername,
            String authorUsername,
            TaskStatus status,
            String description,
            Double expenses,
            Long eventId,
            String url
    ) {
        log.info("Create task with title: {}", title);
        User assigneeUser = (assigneeUsername != null) ? userService.getByUsername(assigneeUsername) : null;
        User authorUser = userService.getByUsername(authorUsername);
        Event event = eventService.getById(eventId);

        Task task = new Task(0L, title, assigneeUser, status, description, expenses, authorUser, event, url);

        task = taskRepository.save(task);
        log.info("Task created with id: {}", task.getId());
        webSocketSessionService.sendMessageToAll(ServerChange.TASKS_UPDATED.name());
        return task;
    }

    public Task update(Long id, String title, String assigneeUsername, String authorUsername, TaskStatus status, String description, Double expenses, String url) {
        log.info("Try to update task with id: {}", id);
        Task task = getById(id);

        User assigneeUser = (assigneeUsername != null) ? userService.getByUsername(assigneeUsername) : null;
        User authorUser = userService.getByUsername(authorUsername);

        task.setTitle(title);
        task.setAssignee(assigneeUser);
        task.setStatus(status);
        task.setDescription(description);
        task.setExpenses(expenses);
        task.setAuthor(authorUser);
        task.setUrl(url);

        task = taskRepository.save(task);
        log.info("Task updated with id: {}", task.getId());
        webSocketSessionService.sendMessageToAll(ServerChange.TASKS_UPDATED.name());
        return task;
    }

    public Task getById(Long id) {
        log.info("Try to get task with id: {}", id);
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    public Page<Task> getAll(int page, int size, long eventId) {
        log.info("Try to get all tasks");
        Pageable pageable = PageRequest.of(page, size);
        return taskRepository.findAllByEventId(eventId, pageable);
    }

    public List<Task> getAllByEventID(Long eventId) {
        log.info("Try to get all tasks");
        return taskRepository.findAllByEventId(eventId);
    }

    public void delete(Long id) {
        log.info("Try to delete task with id: {}", id);
        taskRepository.deleteById(id);
        webSocketSessionService.sendMessageToAll(ServerChange.TASKS_UPDATED.name());
    }
}
