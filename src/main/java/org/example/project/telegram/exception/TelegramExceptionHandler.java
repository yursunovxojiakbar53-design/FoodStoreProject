package org.example.project.telegram.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project.telegram.enums.BotLanguage;
import org.example.project.telegram.i18n.MessageService;
import org.example.project.telegram.service.BotMessenger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramExceptionHandler {

    private final MessageService messageService;
    private final BotMessenger messenger;

    public void handle(Long chatId, Exception ex) {
        log.error("Telegram bot error for chat {} [{}]: {}", chatId, ex.getClass().getSimpleName(), ex.getMessage(), ex);
        if (chatId == null) return;
        try {
            String text;
            if (ex instanceof TelegramBotException) {
                text = ex.getMessage();
            } else {
                text = messageService.get("error.generic", BotLanguage.UZ)
                        + "\n[" + ex.getClass().getSimpleName() + ": " + ex.getMessage() + "]";
            }
            messenger.sendText(chatId, text);
        } catch (Exception sendEx) {
            log.warn("Failed to send error message to chat {}: {}", chatId, sendEx.getMessage());
        }
    }
}
