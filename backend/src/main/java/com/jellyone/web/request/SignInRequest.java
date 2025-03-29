package com.jellyone.web.request;

import jakarta.validation.constraints.NotNull;

public record SignInRequest(
        @NotNull String username,
        @NotNull String password
) {}
