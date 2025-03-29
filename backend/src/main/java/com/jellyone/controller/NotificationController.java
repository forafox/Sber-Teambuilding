package com.jellyone.controller;

import com.jellyone.service.WebSocketSessionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Task Management")
@SecurityRequirement(name = "JWT")
@RequiredArgsConstructor
public class NotificationController {

    private final WebSocketSessionService sessionService;

    @PostMapping("/notify")
    public String notifyAllClients(@RequestBody String message) {
        log.info("Received request to send notification to all clients: {}", message);
        sessionService.sendMessageToAll(message);
        return "Notification sent to all clients";
    }
}