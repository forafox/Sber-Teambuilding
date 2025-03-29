package com.jellyone.web.response;

import com.jellyone.domain.Chat;
import com.jellyone.domain.Message;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ChatResponse(
        @NotNull(message = "Chat id must not be null")
        Long id,
        @NotNull(message = "Array must not be null")
        List<MessageResponse> messages,
        @NotNull(message = "Array must not be null")
        List<MessageResponse> pinnedMessages
) {

    public static ChatResponse toResponse(Chat chat, List<Message> pinnedMessages) {
        return new ChatResponse(
                chat.getId(),
                chat.getMessages().stream().map(MessageResponse::toResponse).toList(),
                pinnedMessages.stream().map(MessageResponse::toResponse).toList()
        );
    }
}
