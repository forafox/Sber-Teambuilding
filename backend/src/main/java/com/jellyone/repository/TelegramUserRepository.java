package com.jellyone.repository;

import com.jellyone.domain.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {

    Optional<TelegramUser> findByTelegramUsername(String telegramUsername);
    Optional<TelegramUser> findByTelegramChatId(Long telegramChatId);
    Optional<TelegramUser> findTelegramUserByUser_Username(String userName);
}
