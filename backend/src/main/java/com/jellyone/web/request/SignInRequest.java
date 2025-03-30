package com.jellyone.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents user credentials for authentication.
 * Used for signing in to the system and obtaining JWT tokens.
 */
@Schema(description = "User sign-in credentials request payload")
public record SignInRequest(
        @Schema(
                description = "Unique username for authentication",
                example = "john_doe",
                minLength = 4,
                maxLength = 32,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Username must not be blank")
        @Size(min = 4, max = 32, message = "Username must be 4-32 characters")
        String username,

        @Schema(
                description = "User's password for authentication",
                example = "P@ssw0rd123!",
                minLength = 5,
                maxLength = 64,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Password must not be blank")
        @Size(min = 5, max = 64, message = "Password must be 8-64 characters")
        String password
) {
}