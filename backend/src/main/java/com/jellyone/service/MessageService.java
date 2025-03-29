package com.jellyone.service;

import com.jellyone.domain.Chat;
import com.jellyone.domain.Message;
import com.jellyone.domain.Poll;
import com.jellyone.domain.User;
import com.jellyone.domain.enums.ServerChange;
import com.jellyone.exception.ResourceNotFoundException;
import com.jellyone.repository.MessageRepository;
import com.jellyone.web.request.PollRequest;
import com.jellyone.web.request.PollUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChatService chatService;
    private final PollService pollService;
    private final WebSocketSessionService webSocketSessionService;

    public Message create(Long chatId, String content, Long replyToMessageId, String username, boolean pinned, PollRequest pollRequest) {
        log.info("Try to create message with content: {}", content);
        Chat chat = chatService.getById(chatId);
        User author = userService.getByUsername(username);
        Poll poll = pollService.create(pollRequest);
        Message message = messageRepository.save(new Message(0L, chat, author, content, LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)
                , replyToMessageId, pinned, poll));
        log.info("Message created with id: {}", message.getId());
        webSocketSessionService.sendMessageToAll(ServerChange.MESSAGES_UPDATED.name());
        return message;
    }

    public Message get(Long id) {
        log.info("Try to get message with id: {}", id);
        return messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
    }

    public void delete(Long id) {
        log.info("Try to delete message with id: {}", id);
        messageRepository.deleteById(id);
        webSocketSessionService.sendMessageToAll(ServerChange.MESSAGES_UPDATED.name());
    }

    public List<Message> getAllByChatId(Long chatId) {
        log.info("Try to get all messages by chat id: {}", chatId);
        return messageRepository.findAllByChatIdOrderByTimestampAsc(chatId);
    }

    public Message update(Long id, String content, Long replyToMessageId, boolean pinned, PollUpdateRequest pollRequest) {
        log.info("Try to update message with id: {}", id);
        Message message = get(id);
        message.setContent(content);
        message.setReplyToMessageId(replyToMessageId);
        message.setPinned(pinned);
        message.setPoll(pollService.update(pollRequest));
        message = messageRepository.save(message);
        log.info("Message updated with id: {}", message.getId());
        webSocketSessionService.sendMessageToAll(ServerChange.MESSAGES_UPDATED.name());
        return message;
    }

    public List<Message> getPinnedMessages(Long chatId) {
        log.info("Try to get pinned messages");
        return messageRepository.findAllByChatIdAndPinnedTrue(chatId);
    }
}
