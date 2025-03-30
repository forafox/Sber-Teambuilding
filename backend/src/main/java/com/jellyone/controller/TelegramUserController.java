package com.jellyone.controller;

import com.jellyone.exception.ErrorMessage;
import com.jellyone.service.TelegramUserService;
import com.jellyone.web.request.TelegramUserRequest;
import com.jellyone.web.response.TelegramUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/telegram-users")
@RequiredArgsConstructor
@Tag(name = "Telegram Management",
        description = "Endpoints for managing Telegram user associations")
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
        @ApiResponse(responseCode = "409",
                description = "Conflict - Telegram user already exists",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "500",
                description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
})
public class TelegramUserController {
    private final TelegramUserService telegramUserService;

    @PostMapping()
    @Operation(summary = "Create Telegram user association",
            description = """
                    Links a Telegram username with the authenticated user account.
                    """,
            operationId = "createTelegramUser")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Telegram user association created successfully",
                    content = @Content(schema = @Schema(implementation = TelegramUserResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public TelegramUserResponse createTelegramUser(
            @Parameter(description = "Telegram user details", required = true)
            @Valid @RequestBody TelegramUserRequest telegramUserRequest,

            Principal principal) {
        log.info("Received request to create Telegram user association for username: {}",
                telegramUserRequest.telegramUsername());
        return TelegramUserResponse.toResponse(telegramUserService.create(
                principal.getName(),
                telegramUserRequest.telegramUsername()
        ));
    }
}