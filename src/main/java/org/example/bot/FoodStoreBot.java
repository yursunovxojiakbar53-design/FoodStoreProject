package org.example.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
public class FoodStoreBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                long chatId = update.getMessage().getChatId();

                log.info("Received message from user {}: {}", chatId, messageText);

                String response = handleMessage(messageText);

                sendMessage(chatId, response);
            }
        } catch (Exception e) {
            log.error("Error processing update: {}", e.getMessage(), e);
        }
    }

    private void sendMessage(long chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        execute(message);
        log.info("Message sent to user: {}", chatId);
    }

    private String handleMessage(String text) {
        return switch (text.trim().toLowerCase()) {
            case "/start" -> "🍕 Assalomu alaikum! FoodStore-ga xush kelibsiz!\n" +
                    "Quyidagi buyruqlarni ishlating:\n" +
                    "/menu - Menyu\n" +
                    "/orders - Buyurtmalar\n" +
                    "/help - Yordam";

            case "/menu" -> "🍕 Bizning kategoriyalarimiz:\n" +
                    "1. Pizza\n" +
                    "2. Burgers\n" +
                    "3. Ichimliklar\n" +
                    "4. Salatlar";

            case "/orders" -> "📦 Sizning buyurtmalaringiz:\n" +
                    "Buyurtma jo'nab topilmadi.";

            case "/help" -> "ℹ️ Yordam:\n" +
                    "/start - Boshlan\n" +
                    "/menu - Menyu ko'rish\n" +
                    "/orders - Buyurtmalar\n" +
                    "/help - Bu xabar";

            default -> "❌ Noto'g'ri buyruq. /help -ni bosing yordam uchun.";
        };
    }
}
