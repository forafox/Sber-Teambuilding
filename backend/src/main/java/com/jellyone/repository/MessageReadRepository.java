package com.jellyone.repository;

import com.jellyone.domain.Chat;
import com.jellyone.domain.MessageRead;
import com.jellyone.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageReadRepository extends JpaRepository<MessageRead, Long> {

    List<MessageRead> findAllByChatId(Long chatId);

    boolean existsByChatAndUser(Chat chat, User user);

    MessageRead findByChatAndUser(Chat chat, User user);
}
