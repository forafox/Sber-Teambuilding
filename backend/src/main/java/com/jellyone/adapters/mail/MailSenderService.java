package com.jellyone.adapters.mail;

import com.jellyone.domain.Event;
import com.jellyone.domain.enums.EventStatus;
import com.jellyone.mail.api.MailApi;
import com.jellyone.mail.api.Sender;
import com.jellyone.mail.dto.TaskDTO;
import com.jellyone.service.EventService;
import com.jellyone.service.TaskService;
import com.jellyone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
@Profile("mail")
public class MailSenderService implements SenderService {
    private final TaskService taskService;
    private final EventService eventService;
    private final MailApi mailApi;
    private final MailTaskToTaskDTO taskToTaskDTO;
    private final Sender sender;
    private final UserService userService;


    @Value("${HOST}")
    private String host;

    public void sendMail(String email, Long userId, Long eventId) {
        Context context = getContext(userId, eventId);
        String eventTitle = eventService.getById(eventId).getTitle();
        sender.sendMailReport(email, eventTitle, context);
    }

    public void sendMail(Long userId, Long eventId) {
        Context context = getContext(userId, eventId);
        String eventTitle = eventService.getById(eventId).getTitle();
        sender.sendMailReport(userService.getById(userId).getEmail(), eventTitle, context);
    }

    private Context getContext(Long userId, Long eventId) {
        Event event = eventService.getById(eventId);
        double totalAmount = mailApi.getTotalSum(eventId); // TODO mailApi поменять на его реализацию
        double amountUserSpent = mailApi.getSumSpentByUser(eventId, userId);
        double amountOwedToUser = mailApi.getSumOwedToUser(eventId, userId);
        boolean isEventClosed = event.getStatus() == EventStatus.DONE;
        LocalDateTime eventEndDate = event.getStatus() == EventStatus.DONE ? event.getDate() : null;
        List<TaskDTO> tasksDTO = taskService.getAll(0, 50, eventId).get().map(taskToTaskDTO::taskToTaskDTO).toList();
        String eventUrl = host + "/events/" + eventId; // TODO + "/tasks"

        return sender.createContext(
                eventUrl,
                event.getTitle(),
                LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC),
                totalAmount,
                amountUserSpent,
                amountOwedToUser,
                isEventClosed,
                eventEndDate,
                tasksDTO);
    }
}