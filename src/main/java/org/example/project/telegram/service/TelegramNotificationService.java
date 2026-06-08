package org.example.project.telegram.service;

import lombok.RequiredArgsConstructor;
import org.example.project.enums.OrderStatus;
import org.example.project.telegram.enums.BotLanguage;
import org.example.project.telegram.entity.TelegramOrderTracking;
import org.example.project.telegram.i18n.MessageService;
import org.example.project.telegram.repository.TelegramOrderTrackingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService {

    private final TelegramOrderTrackingRepository trackingRepository;
    private final MessageService messageService;
    private final BotMessenger messenger;

    @Transactional
    public void trackOrder(int orderId, Long chatId, OrderStatus status) {
        trackingRepository.findByOrderId(orderId).ifPresentOrElse(
                tracking -> {
                    tracking.setLastStatus(status);
                    trackingRepository.save(tracking);
                },
                () -> trackingRepository.save(TelegramOrderTracking.builder()
                        .orderId(orderId)
                        .chatId(chatId)
                        .lastStatus(status)
                        .build())
        );
    }

    public void notifyUser(Long chatId, org.example.project.telegram.enums.BotLanguage lang, OrderStatus status, int orderId) {
        String emoji = switch (status) {
            case NEW, PENDING -> "🆕";
            case CONFIRMED -> "✅";
            case ON_THE_WAY -> "🚚";
            case DELIVERED -> "📦";
            case CANCELED -> "❌";
        };
        String text = emoji + " " + messageService.get("notification.status", lang) + " #" + orderId + " → " + status.name();
        send(chatId, text);
    }

    public void notifyAdmins(java.util.List<Long> adminChatIds, String text) {
        for (Long chatId : adminChatIds) {
            send(chatId, text);
        }
    }

    public void notifyAdminsWithPhoto(java.util.List<Long> adminChatIds, String photoFileId,
                                       String caption, InlineKeyboardMarkup keyboard) {
        for (Long chatId : adminChatIds) {
            messenger.sendPhotoById(chatId, photoFileId, caption, keyboard);
        }
    }

    public void send(Long chatId, String text) {
        messenger.sendText(chatId, text);
    }

    @Transactional
    public void findAndNotify(int orderId, OrderStatus status) {
        trackingRepository.findByOrderId(orderId).ifPresent(tracking -> {
            notifyUser(tracking.getChatId(), BotLanguage.UZ, status, orderId);
            tracking.setLastStatus(status);
            trackingRepository.save(tracking);
        });
    }
}
