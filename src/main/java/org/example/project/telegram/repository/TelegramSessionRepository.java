package org.example.project.telegram.repository;

import org.example.project.telegram.entity.TelegramSession;
import org.example.project.telegram.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TelegramSessionRepository extends JpaRepository<TelegramSession, Long> {
    Optional<TelegramSession> findByTelegramUser(TelegramUser telegramUser);

    @Query("SELECT s FROM TelegramSession s WHERE s.telegramUser.chatId = :chatId")
    Optional<TelegramSession> findByChatId(@Param("chatId") Long chatId);
}
