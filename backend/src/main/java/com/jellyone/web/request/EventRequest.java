package com.jellyone.web.request;

import com.jellyone.domain.enums.EventStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record EventRequest(
        @NotNull String title,
        String description,
        String location,
        @NotNull
        EventStatus status,
        @NotNull LocalDateTime date,
        List<@NotNull Long> participants
) {
}
