package com.jellyone.gigachat.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record LLMResponse(
        @NotNull
        String title,
        @NotNull
        List<String> tasks
) {
}
