package com.jellyone.web.request;

import com.jellyone.domain.enums.EventStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data transfer object for creating or updating events.
 * Contains all necessary information to manage events in the system.
 */
@Schema(description = "Event creation/update request payload")
public record EventRequest(
        @Schema(
                description = "Title of the event",
                example = "Team Building Workshop",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull String title,

        @Schema(
                description = "Detailed description of the event",
                example = "Quarterly team building activities and training",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String description,

        @Schema(
                description = "Physical or virtual location of the event",
                example = "Conference Room A or Zoom Meeting",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String location,

        @Schema(
                description = "Current status of the event",
                allowableValues = {"PLANNED", "IN_PROGRESS", "COMPLETED", "CANCELLED"},
                example = "PLANNED",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull EventStatus status,

        @Schema(
                description = "Date and time when the event starts",
                example = "2023-12-15T09:00:00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull LocalDateTime date,

        @Schema(
                description = "List of participant user IDs",
                example = "[1, 2, 3]",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        List<@NotNull Long> participants
) {
}