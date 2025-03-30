package com.jellyone.controller;

import com.jellyone.exception.ErrorMessage;
import com.jellyone.service.WebSocketSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Notification API",
        description = "Endpoints for managing real-time notifications via WebSocket")
@SecurityRequirement(name = "JWT")
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful operation"),
        @ApiResponse(responseCode = "400",
                description = "Invalid input parameters or malformed request",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "401",
                description = "Unauthorized - Invalid or expired JWT token",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "403",
                description = "Forbidden - Insufficient permissions",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "500",
                description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
})
public class NotificationController {

    private final WebSocketSessionService sessionService;

    @PostMapping("/notify")
    @Operation(summary = "Broadcast notification",
            description = """
                    Sends a real-time notification to all connected WebSocket clients.
                    Requires valid JWT authentication.
                    """,
            operationId = "broadcastNotification")
    @ApiResponse(responseCode = "200",
            description = "Notification successfully broadcasted",
            content = @Content(schema = @Schema(example = "Notification sent to all clients")))
    public String notifyAllClients(
            @Parameter(description = "Notification message content",
                    required = true,
                    example = "{\"type\":\"announcement\",\"content\":\"System maintenance scheduled\"}")
            @RequestBody String message) {
        log.info("Received request to send notification to all clients: {}", message);
        sessionService.sendMessageToAll(message);
        return "Notification sent to all clients";
    }
}