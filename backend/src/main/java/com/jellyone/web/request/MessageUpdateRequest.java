package com.jellyone.web.request;

import jakarta.validation.constraints.NotNull;

public record MessageUpdateRequest(
        @NotNull(message = "Message id must not be null")
        Long id,
        @NotNull(message = "Content must not be null")
        String content,
        Long replyToMessageId,
        @NotNull(message = "Pinned must not be null")
        boolean pinned,
        PollUpdateRequest poll
) {
}
