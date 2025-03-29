package com.jellyone.web.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "JWT Response")
public record JwtResponse(
        @NotNull @Schema(description = "User ID") Long id,
        @NotNull @Schema(description = "Username") String username,
        @NotNull @Schema(description = "Access token") String accessToken,
        @NotNull @Schema(description = "Refresh token") String refreshToken
) {}
