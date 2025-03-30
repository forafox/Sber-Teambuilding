package com.jellyone.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents a Telegram username association request.
 * Used to link a Telegram account with a system user.
 */
@Schema(description = "Telegram username association request payload")
public record TelegramUserRequest(
        @Schema(
                description = "Telegram username (without @)",
                example = "johndoe",
                minLength = 1,
                maxLength = 32,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Telegram username must not be empty")
        @Size(min = 1, max = 32, message = "Telegram username must be 5-32 characters")
        String telegramUsername
) {
}