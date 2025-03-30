package com.jellyone.web.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jellyone.domain.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

/**
 * Represents a message response containing message content and metadata.
 * Includes information about the author, timestamps, and message relations.
 */
@Schema(description = "Message response with content and metadata")
public record MessageResponse(
        @Schema(
                description = "Unique identifier of the message",
                example = "456",
                minimum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Message ID must not be null")
        @Positive(message = "Message ID must be a positive number")
        Long id,

        @Schema(
                description = "Text content of the message",
                example = "Hello team! Let's discuss the project updates.",
                minLength = 1,
                maxLength = 2000,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Message content must not be empty")
        String content,

        @Schema(
                description = "Author of the message",
                implementation = UserResponse.class,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Message author must not be null")
        UserResponse author,

        @Schema(
                description = "Timestamp when the message was created",
                example = "2023-12-15T09:00:00Z",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm'Z'", timezone = "UTC")
        @NotNull(message = "Message timestamp must not be null")
        LocalDateTime timestamp,

        @Schema(
                description = "ID of the message this message replies to (null if not a reply)",
                example = "123",
                nullable = true
        )
        Long replyToMessageId,

        @Schema(
                description = "Whether the message is pinned in the chat",
                example = "false",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Pinned status must be specified")
        boolean pinned,

        @Schema(
                description = "Poll attached to the message (null if no poll)",
                implementation = PollResponse.class,
                nullable = true
        )
        PollResponse poll
) {

    /**
     * Converts a Message domain object to a MessageResponse DTO.
     * Handles null poll gracefully by returning null in the response.
     *
     * @param message the message entity to convert
     * @return fully populated MessageResponse
     * @throws IllegalArgumentException if message is null
     */
    public static MessageResponse toResponse(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }

        return new MessageResponse(
                message.getId(),
                message.getContent(),
                UserResponse.toResponse(message.getAuthor()),
                message.getTimestamp(),
                message.getReplyToMessageId(),
                message.isPinned(),
                message.getPoll() != null ? PollResponse.toResponse(message.getPoll()) : null
        );
    }
}