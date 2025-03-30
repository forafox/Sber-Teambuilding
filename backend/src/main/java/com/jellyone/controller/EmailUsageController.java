package com.jellyone.controller;

import com.jellyone.adapters.mail.SenderService;
import com.jellyone.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Email Sender")
@SecurityRequirement(name = "JWT")
@RequiredArgsConstructor
@Profile("mail")
@Tag(name = "Email Management",
        description = "Endpoints for sending email notifications and reports")
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
        @ApiResponse(responseCode = "404",
                description = "Event not found",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "500",
                description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
})
public class EmailUsageController {
    private final SenderService senderService;

    @Operation(summary = "Send email report",
            description = """
                    Sends a formatted email report
                    """,
            operationId = "sendEmailReport")
    @ApiResponse(responseCode = "200",
            description = "Email sent successfully")
    @PostMapping("/simple-email")
    public void sendSimpleEmail(
            @RequestParam(defaultValue = "email") String email,
            @RequestParam(defaultValue = "userId") Long userId,
            @RequestParam(defaultValue = "0") Long eventId
    ) {
        log.info("Send simple email");
        senderService.sendMail(email, userId, eventId);
    }
}
