package com.jellyone.web.request;

import jakarta.validation.constraints.NotNull;

public record TelegramUserRequest(
        @NotNull(message = "Username must not be null")
        String telegramUsername
) {
}
