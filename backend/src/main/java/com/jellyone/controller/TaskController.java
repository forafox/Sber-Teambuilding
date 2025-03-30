package com.jellyone.controller;

import com.jellyone.adapters.telegram.TelegramNotificationService;
import com.jellyone.exception.ErrorMessage;
import com.jellyone.service.TaskService;
import com.jellyone.web.request.TaskRequest;
import com.jellyone.web.response.TaskResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/events/{eventId}")
@RequiredArgsConstructor
@Tag(name = "Task Management",
        description = "Endpoints for creating, managing and tracking tasks within events")
@SecurityRequirement(name = "JWT")
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful operation"),
        @ApiResponse(responseCode = "400",
                description = "Invalid input parameters or malformed request",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "401",
                description = "Unauthorized - Invalid or expired JWT token",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "403",
                description = "Forbidden - Insufficient permissions",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "404",
                description = "Resource not found",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "500",
                description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
})
public class TaskController {

    private final TaskService taskService;
    private final TelegramNotificationService telegramNotificationService;

    @GetMapping("/tasks")
    @Operation(summary = "Get paginated tasks",
            description = "Retrieves a paginated list of tasks for a specific event",
            operationId = "getTasks")
    @ApiResponse(responseCode = "200",
            description = "Tasks retrieved successfully",
            content = @Content(schema = @Schema(implementation = TaskResponse.class)))
    public Page<TaskResponse> getTasks(
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,

            @Parameter(description = "ID of the event", required = true, example = "123")
            @PathVariable Long eventId) {
        log.info("Received request to get all tasks for event {}", eventId);
        return taskService.getAll(page, pageSize, eventId).map(TaskResponse::toResponse);
    }

    @PostMapping("/tasks")
    @Operation(summary = "Create new task",
            description = "Creates a new task and notifies assignee via Telegram",
            operationId = "createTask")
    @ApiResponse(responseCode = "200",
            description = "Task created successfully",
            content = @Content(schema = @Schema(implementation = TaskResponse.class)))
    public TaskResponse createTask(
            @Parameter(description = "Task details", required = true)
            @Valid @RequestBody TaskRequest task,

            @Parameter(description = "ID of the event", required = true, example = "123")
            @PathVariable Long eventId,

            Principal principal) {
        log.info("Received request to create task '{}' for event {}", task.title(), eventId);
        telegramNotificationService.sendTaskNotification(
                task.assigneeUsername(),
                task.title(),
                task.description()
        );
        return TaskResponse.toResponse(taskService.create(
                task.title(),
                task.assigneeUsername(),
                principal.getName(),
                task.status(),
                task.description(),
                task.expenses(),
                eventId,
                task.url()
        ));
    }

    @GetMapping("/tasks/{id}")
    @Operation(summary = "Get task by ID",
            description = "Retrieves details of a specific task",
            operationId = "getTaskById")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Task retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public TaskResponse getTaskById(
            @Parameter(description = "ID of the event", required = true, example = "123")
            @PathVariable Long eventId,

            @Parameter(description = "ID of the task", required = true, example = "456")
            @PathVariable Long id) {
        log.info("Received request to get task {} for event {}", id, eventId);
        return TaskResponse.toResponse(taskService.getById(id));
    }

    @DeleteMapping("/tasks/{id}")
    @Operation(summary = "Delete task",
            description = "Permanently removes a task",
            operationId = "deleteTask")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public void deleteTask(
            @Parameter(description = "ID of the event", required = true, example = "123")
            @PathVariable Long eventId,

            @Parameter(description = "ID of the task", required = true, example = "456")
            @PathVariable Long id) {
        log.info("Received request to delete task {} from event {}", id, eventId);
        taskService.delete(id);
    }

    @PutMapping("/tasks/{id}")
    @Operation(summary = "Update task",
            description = "Modifies all fields of an existing task",
            operationId = "updateTask")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Task updated successfully",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public TaskResponse updateTask(
            @Parameter(description = "ID of the task", required = true, example = "456")
            @PathVariable Long id,

            @Parameter(description = "ID of the event", required = true, example = "123")
            @PathVariable Long eventId,

            @Parameter(description = "Updated task details", required = true)
            @Valid @RequestBody TaskRequest task,

            Principal principal) {
        log.info("Received request to update task {} in event {}", id, eventId);
        return TaskResponse.toResponse(taskService.update(
                id,
                task.title(),
                task.assigneeUsername(),
                principal.getName(),
                task.status(),
                task.description(),
                task.expenses(),
                task.url()
        ));
    }
}