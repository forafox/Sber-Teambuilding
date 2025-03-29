package com.jellyone.controller;

import com.jellyone.service.TaskService;
import com.jellyone.web.request.TaskRequest;
import com.jellyone.web.response.TaskResponse;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Task Management")
@SecurityRequirement(name = "JWT")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Get all tasks")
    @GetMapping("/tasks")
    public Page<TaskResponse> getTasks(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @PathVariable Long eventId
    ) {
        log.info("Received request to get all tasks");
        return taskService.getAll(page, pageSize, eventId).map(TaskResponse::toResponse);
    }

    @Operation(summary = "Create task")
    @PostMapping("/tasks")
    public TaskResponse createTask(
            @Valid @RequestBody TaskRequest task,
            @PathVariable Long eventId,
            Principal principal
    ) {
        log.info("Received request to create a task with title: {}", task.title());
        return TaskResponse.toResponse(taskService.create(
                task.title(),
                task.assigneeUsername(),
                principal.getName(),
                task.status(),
                task.description(),
                task.expenses(),
                eventId
        ));
    }

    @GetMapping("/tasks/{id}")
    public TaskResponse getTaskById(
            @PathVariable Long eventId,
            @PathVariable Long id
    ) {
        log.info("Received request to get a task with id: {}", id);
        return TaskResponse.toResponse(taskService.getById(id));
    }

    @DeleteMapping("/tasks/{id}")
    public void deleteTask(
            @PathVariable Long eventId,
            @PathVariable Long id
    ) {
        log.info("Received request to delete a task with id: {}", id);
        taskService.delete(id);
    }

    @PutMapping("/tasks/{id}")
    public TaskResponse updateTask(
            @PathVariable Long id,
            @PathVariable Long eventId,
            @Valid @RequestBody TaskRequest task,
            Principal principal
    ) {
        log.info("Received request to update a task with id: {}", id);
        return TaskResponse.toResponse(taskService.update(
                id,
                task.title(),
                task.assigneeUsername(),
                principal.getName(),
                task.status(),
                task.description(),
                task.expenses()
        ));
    }
}
