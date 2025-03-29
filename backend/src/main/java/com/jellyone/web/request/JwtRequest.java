package com.jellyone.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Schema(description = "JWT Request", accessMode = Schema.AccessMode.READ_ONLY)
@Builder
public record JwtRequest(
        @Schema(description = "Username")
        @NotNull(message = "Username must not be null")
        String username,

        @Schema(description = "Password")
        @NotNull(message = "Password must not be null")
        String password
) {}
