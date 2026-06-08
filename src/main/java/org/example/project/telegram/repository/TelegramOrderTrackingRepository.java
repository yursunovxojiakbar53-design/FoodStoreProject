package org.example.project.telegram.repository;

import org.example.project.telegram.entity.TelegramOrderTracking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TelegramOrderTrackingRepository extends JpaRepository<TelegramOrderTracking, Long> {
    List<TelegramOrderTracking> findAllByChatId(Long chatId);
    Optional<TelegramOrderTracking> findByOrderId(Integer orderId);
}
