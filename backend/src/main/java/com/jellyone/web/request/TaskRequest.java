package com.jellyone.web.request;

import com.jellyone.domain.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record TaskRequest(
        @NotNull String title,
        String assigneeUsername,
        @NotNull TaskStatus status,
        String description,
        Double expenses,
        String url
) {}
