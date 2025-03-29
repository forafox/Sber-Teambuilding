package com.jellyone.web.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OptionUpdateRequest(
        @NotNull(message = "Id must not be null")
        Long id,
        @NotNull(message = "Title must not be null")
        String title,
        @NotNull(message = "Voters must not be null")
        List<Long> voters

) {
}
