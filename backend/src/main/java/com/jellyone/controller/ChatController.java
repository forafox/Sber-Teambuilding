package com.jellyone.controller;

import com.jellyone.exception.ErrorMessage;
import com.jellyone.service.ChatService;
import com.jellyone.service.MessageService;
import com.jellyone.web.response.ChatResponse;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
@Tag(name = "Chat Management",
        description = "Endpoints for chat creation, retrieval and deletion")
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
        @ApiResponse(responseCode = "500",
                description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
})
public class ChatController {

    private final ChatService chatService;
    private final MessageService messageService;

    @PostMapping
    @Operation(summary = "Create new chat",
            description = "Creates a new chat session and returns chat details",
            operationId = "createChat")
    @ApiResponse(responseCode = "200",
            description = "Chat created successfully",
            content = @Content(schema = @Schema(implementation = ChatResponse.class)))
    public ChatResponse createChat() {
        log.info("Received request to create a chat");
        return ChatResponse.toResponse(chatService.create(), List.of(), Map.of());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get chat by ID",
            description = "Retrieves chat details including pinned and read messages",
            operationId = "getChatById")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Chat retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ChatResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Chat not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ChatResponse getChatById(
            @Parameter(description = "ID of the chat to retrieve",
                    required = true,
                    example = "123")
            @PathVariable Long id) {
        log.info("Received request to get a chat with id: {}", id);
        return ChatResponse.toResponse(chatService.getById(id),
                messageService.getPinnedMessages(id),
                messageService.getAllReadMessagesByChat(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete chat by ID",
            description = "Permanently deletes a chat session",
            operationId = "deleteChat")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Chat deleted successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Chat not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public void deleteChat(
            @Parameter(description = "ID of the chat to delete",
                    required = true,
                    example = "123")
            @PathVariable Long id) {
        log.info("Received request to delete a chat with id: {}", id);
        chatService.deleteById(id);
    }
}