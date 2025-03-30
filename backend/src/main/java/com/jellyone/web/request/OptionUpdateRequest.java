package com.jellyone.web.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Represents a poll option update request containing the option ID, updated title, and voters.
 * Used for modifying existing poll options and tracking votes in messaging system.
 */
@Schema(description = "Poll option update request payload")
public record OptionUpdateRequest(
        @Schema(
                description = "ID of the poll option to update",
                example = "1",
                minimum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Option ID must not be null")
        @Positive(message = "Option ID must be a positive number")
        Long id,

        @Schema(
                description = "Updated title/text of the poll option",
                example = "Strongly Agree",
                minLength = 1,
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Option title must not be empty")
        @Size(min = 1, max = 100, message = "Option title must be between 1 and 100 characters")
        String title,

        @ArraySchema(
                schema = @Schema(
                        description = "User IDs who voted for this option",
                        example = "1",
                        minimum = "1"
                ),
                arraySchema = @Schema(
                        description = "List of voter user IDs",
                        example = "[1, 2, 3]",
                        requiredMode = Schema.RequiredMode.REQUIRED
                )
        )
        @NotNull(message = "Voters list must not be null")
        List<@NotNull(message = "Voter ID must not be null")
        @Positive(message = "Voter ID must be a positive number") Long> voters
) {
}