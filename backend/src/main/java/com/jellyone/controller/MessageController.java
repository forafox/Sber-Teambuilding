package com.jellyone.controller;

import com.jellyone.exception.ErrorMessage;
import com.jellyone.service.MessageReadService;
import com.jellyone.service.MessageService;
import com.jellyone.web.request.MessageRequest;
import com.jellyone.web.request.MessageUpdateRequest;
import com.jellyone.web.response.MessageResponse;
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
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chats/{chatId}/messages")
@RequiredArgsConstructor
@Tag(name = "Message Management",
        description = "Endpoints for creating, reading, updating and deleting chat messages")
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
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @Operation(summary = "Create new message",
            description = "Creates a new message in the specified chat",
            operationId = "createMessage")
    @ApiResponse(responseCode = "200",
            description = "Message created successfully",
            content = @Content(schema = @Schema(implementation = MessageResponse.class)))
    public MessageResponse createMessage(
            @Parameter(description = "ID of the chat", required = true, example = "123")
            @PathVariable Long chatId,

            @Parameter(description = "Message content and metadata", required = true)
            @Valid @RequestBody MessageRequest message,

            Principal principal) {
        log.info("Received request to create a message with content: {}", message.content());
        return MessageResponse.toResponse(
                messageService.create(
                        chatId,
                        message.content(),
                        message.replyToMessageId(),
                        principal.getName(),
                        message.pinned(),
                        message.poll()
                )
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get message by ID",
            description = "Retrieves a specific message from a chat",
            operationId = "getMessageById")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Message retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Message not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public MessageResponse getMessageById(
            @Parameter(description = "ID of the chat", required = true, example = "123")
            @PathVariable Long chatId,
            @Parameter(description = "ID of the message", required = true, example = "456")
            @PathVariable Long id) {
        log.info("Received request to get a message with id: {}", id);
        return MessageResponse.toResponse(messageService.get(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete message",
            description = "Permanently removes a message from a chat",
            operationId = "deleteMessage")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message deleted successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Message not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public void deleteMessage(
            @Parameter(description = "ID of the chat", required = true, example = "123")
            @PathVariable Long chatId,
            @Parameter(description = "ID of the message", required = true, example = "456")
            @PathVariable Long id) {
        log.info("Received request to delete a message with id: {}", id);
        messageService.delete(id);
    }

    @GetMapping
    @Operation(summary = "Get all chat messages",
            description = "Retrieves all messages for a specific chat",
            operationId = "getAllMessagesByChatId")
    @ApiResponse(responseCode = "200",
            description = "Messages retrieved successfully",
            content = @Content(schema = @Schema(implementation = MessageResponse.class)))
    public List<MessageResponse> getAllMessagesByChatId(
            @Parameter(description = "ID of the chat", required = true, example = "123")
            @PathVariable Long chatId) {
        log.info("Received request to get all messages by chat id: {}", chatId);
        return messageService.getAllByChatId(chatId).stream().map(MessageResponse::toResponse).toList();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update message",
            description = "Modifies the content or properties of an existing message",
            operationId = "updateMessage")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Message updated successfully",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Message not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public MessageResponse updateMessage(
            @Parameter(description = "ID of the chat", required = true, example = "123")
            @PathVariable Long chatId,
            @Parameter(description = "ID of the message", required = true, example = "456")
            @PathVariable Long id,
            @Parameter(description = "Updated message content and metadata", required = true)
            @Valid @RequestBody MessageUpdateRequest message) {
        log.info("Received request to update a message with id: {}", id);
        return MessageResponse.toResponse(messageService.update(id, message.content(), message.replyToMessageId(), message.pinned(), message.poll()));
    }

    @PostMapping("/{id}/read")
    @Operation(summary = "Mark message as read",
            description = "Records that a user has read a specific message",
            operationId = "setMessageRead")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message marked as read successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Message not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public void setMessageRead(
            @Parameter(description = "ID of the chat", required = true, example = "123")
            @PathVariable Long chatId,
            @Parameter(description = "ID of the message", required = true, example = "456")
            @PathVariable Long id,

            Principal principal) {
        log.info("Received request to set a message read with id: {}", id);
        messageService.setMessageRead(chatId, id, principal.getName());
    }
}