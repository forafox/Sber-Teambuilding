package com.jellyone.web.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record EventRequest(
        @NotNull String title,
        @NotNull LocalDateTime date,
        List<@NotNull Long> participants
) {}
