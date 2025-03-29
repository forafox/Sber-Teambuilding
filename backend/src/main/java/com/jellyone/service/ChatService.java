package com.jellyone.service;

import com.jellyone.domain.Chat;
import com.jellyone.domain.Event;
import com.jellyone.domain.enums.ServerChange;
import com.jellyone.exception.ResourceNotFoundException;
import com.jellyone.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final WebSocketSessionService webSocketSessionService;

    public Chat create() {
        log.info("Try to create chat");
        Chat chat = chatRepository.save(new Chat(0L, new ArrayList<>()));
        log.info("Chat created with id: {}", chat.getId());
        webSocketSessionService.sendMessageToAll(ServerChange.CHATS_UPDATED.name());
        return chat;
    }

    public Chat getById(Long id) {
        log.info("Try to get chat with id: {}", id);
        return chatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));
    }

    public void deleteById(Long chatId) {
        log.info("Try to delete chat by chat id: {}", chatId);
        chatRepository.deleteById(chatId);
        webSocketSessionService.sendMessageToAll(ServerChange.CHATS_UPDATED.name());
    }
}
