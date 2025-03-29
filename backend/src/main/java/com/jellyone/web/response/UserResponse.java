package com.jellyone.web.response;

import com.jellyone.domain.User;
import com.jellyone.domain.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "User Response")
public record UserResponse(
        @NotNull @Schema(description = "The ID of the user", example = "1") Long id,
        @NotNull @Schema(description = "The username of the user", example = "j.doe") String username,
        @NotNull @Schema(description = "The name of the user", example = "John Doe") String name,
        @NotNull @Schema(description = "The email of the user", example = "john.doe@example.com") String email,
        @NotNull @Schema(description = "The role of the user", example = "ADMIN") Role role
) {
    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
