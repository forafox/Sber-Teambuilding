package com.jellyone.web.response;

import jakarta.validation.constraints.NotNull;

public record TelegramUserResponse(
        @NotNull Long id,
        @NotNull String telegramUsername,
        Long telegramChatId,
        @NotNull UserResponse user
) {
    public static TelegramUserResponse toResponse(com.jellyone.domain.TelegramUser telegramUser) {
        return new TelegramUserResponse(
                telegramUser.getId(),
                telegramUser.getTelegramUsername(),
                telegramUser.getTelegramChatId(),
                UserResponse.toResponse(telegramUser.getUser())
        );
    }
}
