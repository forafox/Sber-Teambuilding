package com.jellyone.web.request;

import com.jellyone.domain.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Represents a task creation or update request.
 * Used for managing tasks in the system.
 */
@Schema(description = "Task creation/update request payload")
public record TaskRequest(
        @Schema(
                description = "Title of the task",
                example = "Implement user authentication",
                minLength = 3,
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Task title must not be blank")
        @Size(min = 3, max = 100, message = "Title must be 3-100 characters")
        String title,

        @Schema(
                description = "Username of the assigned user",
                example = "john_dev",
                nullable = true
        )
        String assigneeUsername,

        @Schema(
                description = "Current status of the task",
                implementation = TaskStatus.class,
                allowableValues = {"IN_PROGRESS", "DONE"},
                example = "TODO",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Task status must be specified")
        TaskStatus status,

        @Schema(
                description = "Detailed description of the task",
                example = "Implement JWT authentication for the API",
                maxLength = 1000,
                nullable = true
        )
        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description,

        @Schema(
                description = "Expenses associated with the task",
                example = "150.50",
                minimum = "0",
                nullable = true
        )
        @PositiveOrZero(message = "Expenses must be positive or zero")
        Double expenses,

        @Schema(
                description = "URL related to the task",
                example = "https://example.com/task-docs",
                nullable = true
        )
        String url
) {
}