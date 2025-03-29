package com.jellyone.controller;

import com.jellyone.service.MessageReadService;
import com.jellyone.service.MessageService;
import com.jellyone.service.PollService;
import com.jellyone.web.request.MessageRequest;
import com.jellyone.web.request.MessageUpdateRequest;
import com.jellyone.web.response.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chats/{chatId}/messages")
@Tag(name = "Message Management")
@SecurityRequirement(name = "JWT")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final MessageReadService messageReadService;

    @Operation(summary = "Create message")
    @PostMapping
    public MessageResponse createMessage(
            @PathVariable Long chatId,
            @RequestBody MessageRequest message,
            Principal principal
    ) {
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

    @Operation(summary = "Get message by id")
    @GetMapping("/{id}")
    public MessageResponse getMessageById(
            @PathVariable Long chatId,
            @PathVariable Long id
    ) {
        log.info("Received request to get a message with id: {}", id);
        return MessageResponse.toResponse(messageService.get(id));
    }

    @Operation(summary = "Delete message by id")
    @DeleteMapping("/{id}")
    public void deleteMessage(
            @PathVariable Long chatId,
            @PathVariable Long id
    ) {
        log.info("Received request to delete a message with id: {}", id);
        messageService.delete(id);
    }

    @Operation(summary = "Get all messages by chat id")
    @GetMapping
    public List<MessageResponse> getAllMessagesByChatId(
            @PathVariable Long chatId
    ) {
        log.info("Received request to get all messages by chat id: {}", chatId);
        return messageService.getAllByChatId(chatId).stream().map(MessageResponse::toResponse).toList();
    }

    @Operation(summary = "Update message by id")
    @PutMapping("/{id}")
    public MessageResponse updateMessage(
            @PathVariable Long chatId,
            @PathVariable Long id,
            @RequestBody MessageUpdateRequest message
    ) {
        log.info("Received request to update a message with id: {}", id);
        return MessageResponse.toResponse(messageService.update(id, message.content(), message.replyToMessageId(), message.pinned(), message.poll()));
    }

    @Operation(summary = "Set message read")
    @PostMapping("/{id}/read")
    public void setMessageRead(
            @PathVariable Long chatId,
            @PathVariable Long id,
            Principal principal
    ) {
        log.info("Received request to set a message read with id: {}", id);
        messageService.setMessageRead(chatId, id, principal.getName());
    }
}
