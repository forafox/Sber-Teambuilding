package com.jellyone.web.response;

import com.jellyone.domain.Task;
import com.jellyone.domain.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;

@Schema(description = "Task Response")
public record TaskResponse(
        @NotNull @Schema(description = "Task ID") Long id,
        @NotNull @Schema(description = "Task Title") String title,
        @Schema(description = "Assignee") UserResponse assignee,
        @Schema(description = "Author") UserResponse author,
        @NotNull @Schema(description = "Task Status") TaskStatus status,
        @Schema(description = "Task Description") String description,
        @Schema(description = "Expenses") Double expenses
) {
    public static TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                Optional.ofNullable(task.getAssignee()).map(UserResponse::toResponse).orElse(null),
                Optional.ofNullable(task.getAuthor()).map(UserResponse::toResponse).orElse(null),
                task.getStatus(),
                task.getDescription(),
                task.getExpenses()
        );
    }
}
