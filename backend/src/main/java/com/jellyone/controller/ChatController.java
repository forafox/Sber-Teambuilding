package com.jellyone.controller;


import com.jellyone.service.ChatService;
import com.jellyone.web.response.ChatResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/chats")
@Tag(name = "Chat Management")
@SecurityRequirement(name = "JWT")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "Create chat")
    @PostMapping
    public ChatResponse createChat(
    ) {
        log.info("Received request to create a chat");
        return ChatResponse.toResponse(chatService.create());
    }

    @Operation(summary = "Get chat by id")
    @GetMapping("/{id}")
    public ChatResponse getChatById(
            @PathVariable Long id
    ) {
        log.info("Received request to get a chat with id: {}", id);
        return ChatResponse.toResponse(chatService.getById(id));
    }

    @Operation(summary = "Delete chat by id")
    @DeleteMapping("/{id}")
    public void deleteChat(
            @PathVariable Long id
    ) {
        log.info("Received request to delete a chat with id: {}", id);
        chatService.deleteById(id);
    }
}
