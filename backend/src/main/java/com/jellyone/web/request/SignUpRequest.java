package com.jellyone.web.request;

import jakarta.validation.constraints.NotNull;

public record SignUpRequest(
        @NotNull String username,
        @NotNull String password,
        @NotNull String name,
        @NotNull String email
) {}
