package com.jellyone.controller;

import com.jellyone.adapters.mail.MailTaskToTaskDTO;
import com.jellyone.adapters.mail.SendMail;
import com.jellyone.domain.Event;
import com.jellyone.domain.enums.EventStatus;
import com.jellyone.mail.MailSender;
import com.jellyone.mail.api.MailApi;
import com.jellyone.service.EventService;
import com.jellyone.service.TaskService;
import com.jellyone.service.UserService;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Email Sender")
@SecurityRequirement(name = "JWT")
@RequiredArgsConstructor
@Profile("mail")
public class EmailUsageController {
    private final SendMail sendMail;

    @Operation(summary = "Send simple email")
    @PostMapping("/simple-email")
    public void sendSimpleEmail(
            @RequestParam(defaultValue = "email") String email,
            @RequestParam(defaultValue = "userId") Long userId,
            @RequestParam(defaultValue = "0") Long eventId
    ) {
        log.info("Send simple email");
        sendMail.sendMail(email, userId, eventId);
    }
}
