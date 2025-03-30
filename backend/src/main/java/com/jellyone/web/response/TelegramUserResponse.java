package com.jellyone.web.response;

import com.jellyone.domain.TelegramUser;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Represents a Telegram user association response.
 * Contains the linkage between a system user and their Telegram account.
 */
@Schema(description = "Telegram user association response")
public record TelegramUserResponse(
        @Schema(
                description = "Unique identifier of the Telegram user association",
                example = "1",
                minimum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Association ID must not be null")
        @Positive(message = "Association ID must be a positive number")
        Long id,

        @Schema(
                description = "Telegram username (without @)",
                example = "johndoe",
                minLength = 1,
                maxLength = 32,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Telegram username must not be empty")
        @Size(min = 1, max = 32, message = "Telegram username must be 5-32 characters")
        String telegramUsername,

        @Schema(
                description = "Telegram chat ID for direct communication",
                example = "123456789",
                nullable = true
        )
        Long telegramChatId,

        @Schema(
                description = "Associated system user details",
                implementation = UserResponse.class,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "User details must not be null")
        UserResponse user
) {

    /**
     * Converts a TelegramUser domain object to a TelegramUserResponse DTO.
     *
     * @param telegramUser the Telegram user entity to convert
     * @return fully populated TelegramUserResponse
     * @throws IllegalArgumentException if telegramUser is null
     */
    public static TelegramUserResponse toResponse(TelegramUser telegramUser) {
        if (telegramUser == null) {
            throw new IllegalArgumentException("TelegramUser cannot be null");
        }

        return new TelegramUserResponse(
                telegramUser.getId(),
                telegramUser.getTelegramUsername(),
                telegramUser.getTelegramChatId(),
                UserResponse.toResponse(telegramUser.getUser())
        );
    }
}