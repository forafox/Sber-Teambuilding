package com.jellyone.web.response;

import com.jellyone.domain.Option;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Represents a poll option response containing the option details and voters.
 * Includes information about the option text and users who voted for it.
 */
@Schema(description = "Poll option response with voters information")
public record OptionResponse(
        @Schema(
                description = "Unique identifier of the poll option",
                example = "1",
                minimum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Option ID must not be null")
        @Positive(message = "Option ID must be a positive number")
        Long id,

        @Schema(
                description = "Text content of the poll option",
                example = "Agree",
                minLength = 1,
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Option title must not be empty")
        @Size(min = 1, max = 100, message = "Option title must be between 1 and 100 characters")
        String title,

        @ArraySchema(
                arraySchema = @Schema(
                        description = "List of users who voted for this option",
                        requiredMode = Schema.RequiredMode.REQUIRED
                ),
                schema = @Schema(implementation = UserResponse.class)
        )
        @NotNull(message = "Voters list must not be null")
        List<UserResponse> voters
) {

    /**
     * Converts an Option domain object to an OptionResponse DTO.
     *
     * @param option the poll option entity to convert
     * @return fully populated OptionResponse
     * @throws IllegalArgumentException if option is null
     */
    public static OptionResponse toResponse(Option option) {
        if (option == null) {
            throw new IllegalArgumentException("Option cannot be null");
        }

        return new OptionResponse(
                option.getId(),
                option.getTitle(),
                option.getVoters().stream()
                        .map(UserResponse::toResponse)
                        .toList()
        );
    }
}