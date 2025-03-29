package com.jellyone.service;

import com.jellyone.domain.*;
import com.jellyone.domain.enums.EventStatus;
import com.jellyone.domain.enums.Role;
import com.jellyone.domain.enums.TaskStatus;
import com.jellyone.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateService {

    private final EventService eventService;
    private final TaskService taskService;

    private final List<Event> mockEvents = createMockEvents();
    private final List<Task> mockTasks = createMockTasks();

    public Event applyTemplate(Long templateId, String username) {
        Event templateEvent = getTemplateEventById(templateId);

        Event newEvent = eventService.create(
                templateEvent.getTitle(),
                templateEvent.getDescription(),
                templateEvent.getLocation(),
                templateEvent.getStatus(),
                username,
                templateEvent.getDate(),
                null
        );

        List<Task> templateTasks = getAllTemplateTasksByEventId(templateId);
        for (Task templateTask : templateTasks) {
            taskService.create(
                    templateTask.getTitle(),
                    null,
                    username,
                    templateTask.getStatus(),
                    templateTask.getDescription(),
                    templateTask.getExpenses(),
                    newEvent.getId(),
                    templateTask.getUrl()
            );
        }

        return newEvent;
    }

    public List<Event> getAllTemplatesEvent() {
        return mockEvents;
    }

    public Event getTemplateEventById(Long id) {
        return mockEvents.stream()
                .filter(event -> event.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Template event not found with id: " + id));
    }

    public List<Task> getAllTemplateTasksByEventId(Long eventId) {
        return mockTasks.stream()
                .filter(task -> task.getEvent().getId().equals(eventId))
                .toList();
    }

    public Task getTemplateTaskById(Long eventId, Long taskId) {
        return mockTasks.stream()
                .filter(task -> task.getEvent().getId().equals(eventId) && task.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Template task not found with id: " + taskId));
    }

    private List<Event> createMockEvents() {
        // Создаем участников
        User organizer = createUser(1L, "org_user", "Организатор Петров", "org@example.com");
        User participant1 = createUser(2L, "part1", "Участник Иванов", "part1@example.com");
        User participant2 = createUser(3L, "part2", "Участник Сидорова", "part2@example.com");
        User participant3 = createUser(4L, "part2", "Участник Кузнецов", "part3@example.com");

        // Создаем чаты
        Chat shashlikChat = createChat(1L);
        Chat bowlingChat = createChat(2L);
        Chat boardGamesChat = createChat(3L);

        // Создаем события
        Event shashlikEvent = Event.builder()
                .id(1L)
                .title("Шашлыки")
                .description("Выезд на природу, шашлыки и активные игры")
                .location("Лесопарк 'Дубки'")
                .status(EventStatus.IN_PROGRESS)
                .author(organizer)
                .date(LocalDateTime.now().plusDays(7))
                .participants(List.of(organizer, participant1, participant2))
                .chat(shashlikChat)
                .build();

        Event bowlingEvent = Event.builder()
                .id(2L)
                .title("День рождения в боулинге")
                .description("Отмечаем день рождения Максима в боулинге")
                .location("Боулинг-клуб 'Strike'")
                .status(EventStatus.IN_PROGRESS)
                .author(participant3)
                .date(LocalDateTime.now().plusDays(14))
                .participants(List.of(participant3, organizer, participant1))
                .chat(bowlingChat)
                .build();

        Event boardGamesEvent = Event.builder()
                .id(3L)
                .title("Настольные игры")
                .description("Вечер настольных игр для компании друзей")
                .location("Квартира организатора")
                .status(EventStatus.IN_PROGRESS)
                .author(participant2)
                .date(LocalDateTime.now().minusHours(2))
                .participants(List.of(participant2, participant1, participant3))
                .chat(boardGamesChat)
                .build();

        return List.of(shashlikEvent, bowlingEvent, boardGamesEvent);
    }

    private List<Task> createMockTasks() {
        List<Event> events = mockEvents; // или createMockEvents(), если mockEvents еще не инициализирован

        User organizer = events.get(0).getAuthor();
        User participant1 = events.get(0).getParticipants().get(1);
        User participant2 = events.get(0).getParticipants().get(2);
        User participant3 = events.get(1).getAuthor();

        return List.of(
                // Задачи для "Шашлыки" (eventId = 1)
                Task.builder()
                        .id(1L)
                        .title("Купить мясо")
                        .assignee(participant1)
                        .status(TaskStatus.IN_PROGRESS)
                        .description("3 кг свинины для шашлыка")
                        .expenses(2500.0)
                        .author(organizer)
                        .event(events.get(0))
                        .build(),
                Task.builder()
                        .id(2L)
                        .title("Привезти мангал")
                        .assignee(organizer)
                        .status(TaskStatus.IN_PROGRESS)
                        .description("Большой мангал и уголь")
                        .expenses(0.0)
                        .author(organizer)
                        .event(events.get(0))
                        .build(),

                // Задачи для "День рождения в боулинге" (eventId = 2)
                Task.builder()
                        .id(3L)
                        .title("Забронировать дорожки")
                        .assignee(participant3)
                        .status(TaskStatus.DONE)
                        .description("2 дорожки на 3 часа")
                        .expenses(5000.0)
                        .author(participant3)
                        .event(events.get(1))
                        .build(),
                Task.builder()
                        .id(4L)
                        .title("Заказать торт")
                        .assignee(organizer)
                        .status(TaskStatus.DONE)
                        .description("Торт на 10 человек")
                        .expenses(2000.0)
                        .author(participant3)
                        .event(events.get(1))
                        .build(),

                // Задачи для "Настольные игры" (eventId = 3)
                Task.builder()
                        .id(5L)
                        .title("Подготовить игры")
                        .assignee(participant2)
                        .status(TaskStatus.DONE)
                        .description("Манчкин, Колонизаторы, Uno")
                        .expenses(0.0)
                        .author(participant2)
                        .event(events.get(2))
                        .build(),
                Task.builder()
                        .id(6L)
                        .title("Купить закуски")
                        .assignee(participant1)
                        .status(TaskStatus.IN_PROGRESS)
                        .description("Чипсы, орешки, напитки")
                        .expenses(1500.0)
                        .author(participant2)
                        .event(events.get(2))
                        .build()
        );
    }

    private User createUser(Long id, String username, String name, String email) {
        return new User(id, username, "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG", name, email, Role.USER);
    }

    private Chat createChat(Long id) {
        Chat chat = new Chat();
        chat.setId(id);
        return chat;
    }
}