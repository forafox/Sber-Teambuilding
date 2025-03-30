package com.jellyone.web.response;

import com.jellyone.domain.Task;
import com.jellyone.domain.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import java.util.Optional;

/**
 * Represents a task response containing task details and associated information.
 * Includes information about the task, assignee, author, and related metadata.
 */
@Schema(description = "Task response with complete task details")
public record TaskResponse(
        @Schema(
                description = "Unique identifier of the task",
                example = "101",
                minimum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Task ID must not be null")
        @Positive(message = "Task ID must be a positive number")
        Long id,

        @Schema(
                description = "Title of the task",
                example = "Implement user authentication",
                minLength = 3,
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Task title must not be empty")
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        String title,

        @Schema(
                description = "User assigned to complete this task",
                implementation = UserResponse.class,
                nullable = true
        )
        UserResponse assignee,

        @Schema(
                description = "User who created this task",
                implementation = UserResponse.class,
                nullable = true
        )
        UserResponse author,

        @Schema(
                description = "Current status of the task",
                implementation = TaskStatus.class,
                allowableValues = {"TODO", "IN_PROGRESS", "DONE", "BLOCKED"},
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
                description = "Expenses associated with completing this task",
                example = "150.50",
                minimum = "0",
                nullable = true
        )
        @PositiveOrZero(message = "Expenses must be positive or zero")
        Double expenses,

        @Schema(
                description = "URL related to the task (documentation, reference, etc.)",
                example = "https://example.com/task-docs",
                nullable = true
        )
        String url
) {

    /**
     * Converts a Task domain object to a TaskResponse DTO.
     * Handles null assignee and author gracefully by returning null in the response.
     *
     * @param task the task entity to convert
     * @return fully populated TaskResponse
     * @throws IllegalArgumentException if task is null
     */
    public static TaskResponse toResponse(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                Optional.ofNullable(task.getAssignee())
                        .map(UserResponse::toResponse)
                        .orElse(null),
                Optional.ofNullable(task.getAuthor())
                        .map(UserResponse::toResponse)
                        .orElse(null),
                task.getStatus(),
                task.getDescription(),
                task.getExpenses(),
                task.getUrl()
        );
    }
}