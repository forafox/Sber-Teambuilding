package com.jellyone.service;

import com.jellyone.domain.Chat;
import com.jellyone.domain.Event;
import com.jellyone.domain.Task;
import com.jellyone.domain.User;
import com.jellyone.domain.enums.EventStatus;
import com.jellyone.domain.enums.ServerChange;
import com.jellyone.domain.enums.TaskStatus;
import com.jellyone.exception.ResourceAlreadyExistsException;
import com.jellyone.exception.ResourceNotFoundException;
import com.jellyone.gigachat.LLMClient;
import com.jellyone.repository.EventRepository;
import com.jellyone.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final WebSocketSessionService webSocketSessionService;
    private final ChatService chatService;
    private final LLMClient llmClient;
    private final TaskRepository taskRepository;

    public Event create(String title, String description, String location, EventStatus status, String authorUsername, LocalDateTime date, List<Long> participants) {
        log.info("Try to create event with title: {}", title);
        User authorUser = userService.getByUsername(authorUsername);
        List<User> participantUsers = (participants != null)
                ? participants.stream().map(userService::getById).toList()
                : List.of();

        log.info("Trying to create chat with event title: {}", title);
        Chat chat = chatService.create();
        Event event = new Event(0L, title, description, location, status, authorUser, date, participantUsers, chat);
        log.info("Trying to create event with id: {}", event.getId());
        event = eventRepository.save(event);
        webSocketSessionService.sendMessageToAll(ServerChange.EVENTS_UPDATED.name());
        log.info("Chat and event created with id: {}", event.getId());
        return event;
    }

    public Event update(Long id, String title, String description, String location, EventStatus status, LocalDateTime date, List<Long> participants) {
        log.info("Try to update event with id: {}", id);
        Event event = getById(id);
        List<User> participantUsers = (participants != null)
                ? participants.stream().map(userService::getById).collect(Collectors.toList())
                : List.of();

        event.setTitle(title);
        event.setDate(date);
        event.setParticipants(participantUsers);
        event.setDescription(description);
        event.setLocation(location);
        event.setStatus(status);

        event = eventRepository.save(event);
        log.info("Event updated with id: {}", event.getId());
        webSocketSessionService.sendMessageToAll(ServerChange.EVENTS_UPDATED.name());
        return event;
    }

    public Event getById(Long id) {
        log.info("Try to get event with id: {}", id);
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }

    public Page<Event> getAll(String username, EventStatus status, int page, int size) {
        log.info("Try to get all events");
        Pageable pageable = PageRequest.of(page, size);
        User user = userService.getByUsername(username);
        return eventRepository.findAllWithSomeParameters(user.getId(), status, pageable);
    }

    public void delete(Long id) {
        log.info("Try to delete event with id: {}", id);
        getById(id);
        eventRepository.deleteById(id);
        webSocketSessionService.sendMessageToAll(ServerChange.EVENTS_UPDATED.name());
    }

    public Event updateParticipants(Long id, List<Long> participants) {
        log.info("Try to update event with id: {}", id);
        Event event = getById(id);
        List<User> participantsFromEvent = event.getParticipants();
        for (Long userId : participants) {
            if (checkIfUserAlreadyParticipant(userId, participantsFromEvent)) {
                throw new ResourceAlreadyExistsException("User already participant");
            } else {
                participantsFromEvent.add(userService.getById(userId));
            }
        }
        event = eventRepository.save(event);
        log.info("Event updated with id: {}", event.getId());
        webSocketSessionService.sendMessageToAll(ServerChange.EVENTS_UPDATED.name());
        return event;
    }

    private boolean checkIfUserAlreadyParticipant(Long userId, List<User> participants) {
        return participants.stream().anyMatch(user -> user.getId().equals(userId));
    }

    @Transactional
    public Event createFromPrompt(String authorUsername, String prompt) {
        User authorUser = userService.getByUsername(authorUsername);

        log.info("Creating event from prompt: {}", prompt);
        var response = this.llmClient.generateEvent(prompt);

        List<User> participantUsers = List.of();

        log.info("Trying to create chat for event with title: {}", response.title());
        Chat chat = chatService.create();
        Event event = new Event(0L, response.title(), "", "", EventStatus.IN_PROGRESS, authorUser, LocalDateTime.now(), participantUsers, chat);
        log.info("Trying to create event with id: {}", event.getId());
        Event savedEvent = eventRepository.save(event);
        webSocketSessionService.sendMessageToAll(ServerChange.EVENTS_UPDATED.name());

        response.tasks().forEach((taskName) -> {
            this.createByName(savedEvent, authorUser, taskName);
        });

        return event;
    }

    private Task createByName(Event event, User author, String title) {
        Task task = new Task();
        task.setTitle(title);
        task.setEvent(event);
        task.setAuthor(author);
        task.setStatus(TaskStatus.IN_PROGRESS);

        return this.taskRepository.save(task);
    }
}
