package com.jellyone.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Represents a message update request containing updated message content and metadata.
 * Used for modifying existing messages in chat conversations.
 */
@Schema(description = "Message update request payload")
public record MessageUpdateRequest(
        @Schema(
                description = "ID of the message to update",
                example = "456",
                minimum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Message ID must not be null")
        @Positive(message = "Message ID must be a positive number")
        Long id,

        @Schema(
                description = "Updated text content of the message",
                example = "Updated: Let's meet at 3pm instead!",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Message content must not be empty")
        String content,

        @Schema(
                description = "Updated ID of the message being replied to (null to remove reply)",
                example = "123",
                nullable = true
        )
        Long replyToMessageId,

        @Schema(
                description = "Updated pinned status of the message",
                example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Pinned status must be specified")
        boolean pinned,

        @Schema(
                description = "Updated poll data (null to remove poll)",
                implementation = PollUpdateRequest.class,
                nullable = true
        )
        PollUpdateRequest poll
) {
}