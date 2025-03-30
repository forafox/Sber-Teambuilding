package com.jellyone.web.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Represents a JWT authentication response containing tokens and user information.
 * Returned after successful login or token refresh operations.
 */
@Schema(
        description = "JWT authentication response containing access and refresh tokens",
        accessMode = Schema.AccessMode.READ_ONLY
)
public record JwtResponse(
        @Schema(
                description = "Unique identifier of the authenticated user",
                example = "123",
                minimum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "User ID must not be null")
        @Positive(message = "User ID must be a positive number")
        Long id,

        @Schema(
                description = "Username of the authenticated user",
                example = "john_doe",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Username must not be blank")
        String username,

        @Schema(
                description = "JWT access token for API authorization",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Access token must not be blank")
        String accessToken,

        @Schema(
                description = "JWT refresh token for obtaining new access tokens",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Refresh token must not be blank")
        String refreshToken
) {
}