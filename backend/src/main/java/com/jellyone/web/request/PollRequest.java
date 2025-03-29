package com.jellyone.web.request;

import com.jellyone.domain.enums.PollType;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PollRequest(
        @NotNull(message = "Title must not be null")
        String title,
        @NotNull(message = "Poll type must not be null")
        PollType pollType,
        @NotNull(message = "Options must not be null")
        List<OptionRequest> options
) {
}
