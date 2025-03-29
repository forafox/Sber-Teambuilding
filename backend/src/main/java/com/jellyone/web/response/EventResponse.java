package com.jellyone.web.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jellyone.domain.Event;
import com.jellyone.domain.enums.EventStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public record EventResponse(
        @NotNull Long id,
        @NotNull String title,
        String description,
        String location,
        @NotNull
        EventStatus status,
        @NotNull UserResponse author,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm'Z'", timezone = "UTC")
        @NotNull LocalDateTime date,
        @NotNull List<UserResponse> participants,
        @NotNull Long chatId
) {
    public static EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getStatus(),
                Optional.ofNullable(event.getAuthor()).map(UserResponse::toResponse).orElse(null),
                event.getDate(),
                event.getParticipants().stream().map(UserResponse::toResponse).toList(),
                event.getChat().getId()
        );
    }
}
