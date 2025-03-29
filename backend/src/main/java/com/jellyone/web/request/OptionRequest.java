package com.jellyone.web.request;

import jakarta.validation.constraints.NotNull;

public record OptionRequest(
        @NotNull(message = "Title must not be null")
        String title
) {
}
