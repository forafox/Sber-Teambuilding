package com.jellyone.service;

import com.jellyone.domain.TelegramUser;
import com.jellyone.domain.User;
import com.jellyone.exception.ResourceNotFoundException;
import com.jellyone.repository.TelegramUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramUserService {
    private final TelegramUserRepository telegramUserRepository;
    private final UserService userService;

    public TelegramUser create(
            String username,
            String telegramUsername
    ) {
        log.info("Try to create telegram user with telegram username: {}", telegramUsername);
        User user = userService.getByUsername(username);
        return telegramUserRepository.save(new TelegramUser(0L, telegramUsername, null, user));
    }

    public TelegramUser updateChatIdByTelegramUsername(
            String telegramUsername,
            Long telegramChatId
    ) {
        log.info("Try to update telegram user with telegram username: {}", telegramUsername);
        TelegramUser telegramUser = getByUsername(telegramUsername);
        telegramUser.setTelegramChatId(telegramChatId);
        return telegramUserRepository.save(telegramUser);
    }

    public TelegramUser getByUsername(String telegramUsername) {
        log.info("Try to get telegram user with username: {}", telegramUsername);
        return telegramUserRepository.findByTelegramUsername(telegramUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Telegram user not found"));
    }

    public TelegramUser getByTelegramChatId(Long telegramChatId) {
        log.info("Try to get telegram user with telegram chat id: {}", telegramChatId);
        return telegramUserRepository.findByTelegramChatId(telegramChatId)
                .orElseThrow(() -> new ResourceNotFoundException("Telegram user not found"));
    }

    public boolean telegramUsernameExists(String telegramUsername) {
        log.info("Try to check if telegram username exists: {}", telegramUsername);
        return telegramUserRepository.findByTelegramUsername(telegramUsername).isPresent();
    }
}
