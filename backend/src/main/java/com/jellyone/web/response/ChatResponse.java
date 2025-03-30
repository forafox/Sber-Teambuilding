package com.jellyone.web.response;

import com.jellyone.domain.Chat;
import com.jellyone.domain.Message;
import com.jellyone.domain.User;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a complete chat response containing messages and metadata.
 * Includes regular messages, pinned messages, and read message indicators.
 */
@Schema(description = "Complete chat response with messages and metadata")
public record ChatResponse(
        @Schema(
                description = "Unique identifier of the chat",
                example = "123",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Chat ID must not be null")
        Long id,

        @ArraySchema(
                arraySchema = @Schema(
                        description = "List of messages in the chat",
                        requiredMode = Schema.RequiredMode.REQUIRED
                ),
                schema = @Schema(implementation = MessageResponse.class)
        )
        @NotNull(message = "Messages list must not be null")
        List<MessageResponse> messages,

        @ArraySchema(
                arraySchema = @Schema(
                        description = "List of pinned messages in the chat",
                        requiredMode = Schema.RequiredMode.REQUIRED
                ),
                schema = @Schema(implementation = MessageResponse.class)
        )
        @NotNull(message = "Pinned messages list must not be null")
        List<MessageResponse> pinnedMessages,

        @Schema(
                description = "Map of read messages by user ID",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Read messages map must not be null")
        Map<Long, MessageResponse> readMessages
) {

    /**
     * Converts domain objects to a ChatResponse DTO.
     *
     * @param chat           the chat entity
     * @param pinnedMessages list of pinned messages
     * @param readMessages   map of read messages by user
     * @return fully populated ChatResponse
     */
    public static ChatResponse toResponse(
            Chat chat,
            List<Message> pinnedMessages,
            Map<User, Message> readMessages) {

        if (chat == null) {
            throw new IllegalArgumentException("Chat cannot be null");
        }

        return new ChatResponse(
                chat.getId(),
                chat.getMessages().stream()
                        .map(MessageResponse::toResponse)
                        .collect(Collectors.toList()),
                pinnedMessages.stream()
                        .map(MessageResponse::toResponse)
                        .collect(Collectors.toList()),
                readMessages.entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> entry.getKey().getId(),
                                entry -> MessageResponse.toResponse(entry.getValue())
                        ))
        );
    }
}