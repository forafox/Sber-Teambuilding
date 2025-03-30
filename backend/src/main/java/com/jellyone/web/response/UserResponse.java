package com.jellyone.web.response;

import com.jellyone.domain.User;
import com.jellyone.domain.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Represents a user response containing user details and role information.
 * Includes all essential user data for API responses.
 */
@Schema(description = "User response with complete user details")
public record UserResponse(
        @Schema(
                description = "Unique identifier of the user",
                example = "1",
                minimum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "User ID must not be null")
        @Positive(message = "User ID must be a positive number")
        Long id,

        @Schema(
                description = "Unique username for authentication",
                example = "j.doe",
                minLength = 4,
                maxLength = 32,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Username must not be empty")
        @Size(min = 4, max = 32, message = "Username must be 4-32 characters")
        String username,

        @Schema(
                description = "Full name of the user",
                example = "John Doe",
                minLength = 2,
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Name must not be empty")
        @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
        String name,

        @Schema(
                description = "Email address of the user",
                example = "john.doe@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Email must not be empty")
        @Email(message = "Email should be valid")
        String email,

        @Schema(
                description = "Role defining user permissions",
                implementation = Role.class,
                allowableValues = {"ADMIN", "USER"},
                example = "USER",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Role must not be null")
        Role role
) {

    /**
     * Converts a User domain object to a UserResponse DTO.
     *
     * @param user the user entity to convert
     * @return fully populated UserResponse
     * @throws IllegalArgumentException if user is null
     */
    public static UserResponse toResponse(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}