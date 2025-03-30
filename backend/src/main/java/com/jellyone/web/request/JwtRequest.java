package com.jellyone.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * Represents an authentication request containing user credentials.
 * Used for JWT token generation during login operations.
 */
@Schema(
        description = "Authentication request payload for JWT token generation",
        accessMode = Schema.AccessMode.READ_ONLY,
        example = "{\"username\":\"john_doe\",\"password\":\"SecurePass123!\"}"
)
@Builder
public record JwtRequest(
        @Schema(
                description = "Unique username for authentication",
                example = "john_doe",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Username must not be empty")
        String username,

        @Schema(
                description = "User's password for authentication",
                example = "SecurePass123!",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Password must not be empty")
        String password
) {
}