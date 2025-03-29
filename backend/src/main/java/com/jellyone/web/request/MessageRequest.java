package com.jellyone.web.request;

import jakarta.validation.constraints.NotNull;

public record MessageRequest(
        @NotNull(message = "Content must not be null")
        String content,
        Long replyToMessageId
) {
}
