package com.jellyone.web.response;

import com.jellyone.domain.Chat;
import com.jellyone.domain.Message;
import com.jellyone.domain.User;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record ChatResponse(
        @NotNull(message = "Chat id must not be null")
        Long id,
        @NotNull(message = "Array must not be null")
        List<MessageResponse> messages,
        @NotNull(message = "Array must not be null")
        List<MessageResponse> pinnedMessages,
        @NotNull(message = "Map must not be null")
        Map<Long, MessageResponse> readMessages
) {

    public static ChatResponse toResponse(Chat chat, List<Message> pinnedMessages, Map<User, Message> readMessages) {
        return new ChatResponse(
                chat.getId(),
                chat.getMessages().stream().map(MessageResponse::toResponse).toList(),
                pinnedMessages.stream().map(MessageResponse::toResponse).toList(),
                readMessages.entrySet().stream().collect(
                        Collectors.toMap(
                                entry -> entry.getKey().getId(),
                                entry -> MessageResponse.toResponse(entry.getValue())
                        ))
        );
    }
}
