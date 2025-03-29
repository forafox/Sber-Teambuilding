package com.jellyone.web.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record MessageResponse(
        @NotNull(message = "Message id must not be null")
        Long id,
        @NotNull(message = "Message content must not be null")
        String content,
        @NotNull(message = "Author must not be null")
        UserResponse author,
        @NotNull(message = "Timestamp must not be null")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm'Z'", timezone = "UTC")
        LocalDateTime timestamp
) {
    public static MessageResponse toResponse(com.jellyone.domain.Message message) {
        return new MessageResponse(
                message.getId(),
                message.getContent(),
                UserResponse.toResponse(message.getAuthor()),
                message.getTimestamp()
        );
    }
}
