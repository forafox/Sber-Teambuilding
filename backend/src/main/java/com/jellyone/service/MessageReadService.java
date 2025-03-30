package com.jellyone.service;

import com.jellyone.domain.Chat;
import com.jellyone.domain.Message;
import com.jellyone.domain.MessageRead;
import com.jellyone.domain.User;
import com.jellyone.repository.MessageReadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageReadService {
    private final MessageReadRepository messageReadRepository;

    public MessageRead create(Message message, Chat chat, User user) {
        log.info("Try to create message read with message id: {}", message.getId());
        if (existsReadMessage(chat, user)) {
            MessageRead messageRead = messageReadRepository.findByChatAndUser(chat, user);
            messageRead.setMessage(message);
            messageRead.setReadAt(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
            return messageReadRepository.save(messageRead);
        }
        return messageReadRepository.save(new MessageRead(0L, chat, message, user, LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)));
    }

    public Map<User, Message> getAllReadMessagesByChat(Chat chat) {
        log.info("Try to get all read messages by chat id: {}", chat.getId());
        return messageReadRepository.findAllByChatId(chat.getId())
                .stream()
                .collect(Collectors.toMap(
                        MessageRead::getUser,
                        MessageRead::getMessage
                ));
    }

    public boolean existsReadMessage(Chat chat, User user) {
        log.info("Try to check if message read with user id: {} exists in chat id: {}", user.getId(), chat.getId());
        return messageReadRepository.existsByChatAndUser(chat, user);
    }
}
