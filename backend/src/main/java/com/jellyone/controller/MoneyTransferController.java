package com.jellyone.controller;

import com.jellyone.exception.ErrorMessage;
import com.jellyone.service.MoneyTransferService;
import com.jellyone.web.request.MoneyTransferRequest;
import com.jellyone.web.response.MoneyTransferResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/events/{eventId}/money-transfers")
@RequiredArgsConstructor
@Tag(name = "Money Transfer",
        description = "Endpoints for creating, managing and querying money transfers")
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
public class MoneyTransferController {

    private final MoneyTransferService moneyTransferService;

    @PostMapping
    @Operation(summary = "Create money transfer",
            description = "Creates a new money transfer between users for a specific event",
            operationId = "createMoneyTransfer")
    @ApiResponse(responseCode = "200",
            description = "Money transfer created successfully",
            content = @Content(schema = @Schema(implementation = MoneyTransferResponse.class)))
    public MoneyTransferResponse createMoneyTransfer(
            @Parameter(description = "ID of the event", required = true, example = "123")
            @PathVariable Long eventId,
            @Parameter(description = "Money transfer details", required = true)
            @RequestBody MoneyTransferRequest request) {
        log.info("Create money transfer for event {}", eventId);
        return MoneyTransferResponse.toResponse(moneyTransferService.create(
                request.amount(),
                request.senderId(),
                request.recipientId(),
                eventId
        ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get money transfer by ID",
            description = "Retrieves a specific money transfer by its ID",
            operationId = "getMoneyTransferById")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Money transfer retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MoneyTransferResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Money transfer not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public MoneyTransferResponse getMoneyTransferById(
            @Parameter(description = "ID of the event", required = true, example = "123")
            @PathVariable Long eventId,
            @Parameter(description = "ID of the money transfer", required = true, example = "456")
            @PathVariable Long id) {
        log.info("Get money transfer by id: {}", id);
        return MoneyTransferResponse.toResponse(moneyTransferService.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all money transfers for event",
            description = "Retrieves all money transfers for a specific event with pagination",
            operationId = "getAllMoneyTransfersByEventId")
    @ApiResponse(responseCode = "200",
            description = "Money transfers retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class)))
    public Page<MoneyTransferResponse> getAllMoneyTransfers(
            @Parameter(description = "ID of the event", required = true, example = "123")
            @PathVariable Long eventId,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "20")
            @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("Get all money transfers for event {}", eventId);
        return moneyTransferService.getAllByEventId(eventId, page, size).map(MoneyTransferResponse::toResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete money transfer",
            description = "Permanently removes a money transfer",
            operationId = "deleteMoneyTransfer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Money transfer deleted successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Money transfer not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public void deleteMoneyTransfer(
            @Parameter(description = "ID of the event", required = true, example = "123")
            @PathVariable Long eventId,
            @Parameter(description = "ID of the money transfer", required = true, example = "456")
            @PathVariable Long id) {
        log.info("Delete money transfer by id: {}", id);
        moneyTransferService.delete(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update money transfer",
            description = "Modifies an existing money transfer",
            operationId = "updateMoneyTransfer")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Money transfer updated successfully",
                    content = @Content(schema = @Schema(implementation = MoneyTransferResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Money transfer not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public MoneyTransferResponse updateMoneyTransfer(
            @Parameter(description = "ID of the event", required = true, example = "123")
            @PathVariable Long eventId,
            @Parameter(description = "ID of the money transfer", required = true, example = "456")
            @PathVariable Long id,
            @Parameter(description = "Updated money transfer details", required = true)
            @RequestBody MoneyTransferRequest request) {
        log.info("Update money transfer by id: {}", id);
        return MoneyTransferResponse.toResponse(moneyTransferService.update(
                id,
                request.amount(),
                request.senderId(),
                request.recipientId(),
                eventId
        ));
    }
}