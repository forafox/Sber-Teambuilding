package com.jellyone.controller;

import com.jellyone.adapters.mail.MailTaskToTaskDTO;
import com.jellyone.mail.MailSender;
import com.jellyone.service.EventService;
import com.jellyone.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Email Sender")
@SecurityRequirement(name = "JWT")
@RequiredArgsConstructor
@Profile("mail")
public class EmailUsageController {
    private final MailSender mailSender;
    private final TaskService taskService;
    private final MailTaskToTaskDTO taskToTaskDTO;
    private final EventService eventService;

    @Operation(summary = "Send simple email")
    @PostMapping("/simple-email")
    public void sendSimpleEmail(
            @RequestParam(defaultValue = "email") String email,
            @RequestParam(defaultValue = "0") Long eventId
    ) {
        log.info("Send simple email");

        Date curDate = new Date(); // передаем время отправки письма
        int totalAmount = 600;
        int amountYouSpent = 300;
        int amountOwedToYou = -100;
        int amountYouOwe = 100; // здесь если отрицательное, тогда не отображать
        // переделать template TODO
        boolean isEventClosed = false;
        Date eventEndDate = null;
        Context context = mailSender.createContext(
                eventId,
                curDate,
                totalAmount,
                amountYouSpent,
                amountOwedToYou,
                amountYouOwe,
                isEventClosed,
                eventEndDate,
                taskService.getAll(0, 30, eventId).map(taskToTaskDTO::taskToTaskDTO).toList()
                );

        String eventTitle = eventService.getById(eventId).getTitle();
        mailSender.sendMailReport(email, eventTitle, context);
    }
}
