package com.jellyone.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a message creation request containing message content and metadata.
 * Used for creating new messages in chat conversations.
 */
@Schema(description = "Message creation request payload")
public record MessageRequest(
        @Schema(
                description = "Text content of the message",
                example = "Hello team! Let's discuss the project updates.",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Message content must not be empty")
        String content,

        @Schema(
                description = "ID of the message being replied to (null if not a reply)",
                example = "123",
                nullable = true
        )
        Long replyToMessageId,

        @Schema(
                description = "Whether the message should be pinned in the chat",
                example = "false",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Pinned status must be specified")
        boolean pinned,

        @Schema(
                description = "Poll attached to the message (null if no poll)",
                implementation = PollRequest.class,
                nullable = true
        )
        PollRequest poll
) {
}