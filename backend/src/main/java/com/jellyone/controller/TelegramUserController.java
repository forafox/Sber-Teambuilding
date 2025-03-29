package com.jellyone.controller;

import com.jellyone.service.TelegramUserService;
import com.jellyone.web.request.TelegramUserRequest;
import com.jellyone.web.response.TelegramUserResponse;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Telegram User Management")
@RestController
@RequestMapping("/api/telegram-users")
@SecurityRequirement(name = "JWT")
@RequiredArgsConstructor
public class TelegramUserController {
    private final TelegramUserService telegramUserService;

    @Operation(summary = "Create telegram user")
    @PostMapping()
    public TelegramUserResponse createTelegramUser(
            @Valid @RequestBody TelegramUserRequest telegramUserRequest,
            Principal principal
    ) {
        log.info("Received request to create a telegram user with username: {}", telegramUserRequest.telegramUsername());
        return TelegramUserResponse.toResponse(telegramUserService.create(
                principal.getName(),
                telegramUserRequest.telegramUsername()
        ));
    }
}
