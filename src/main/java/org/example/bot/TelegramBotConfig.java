package org.example.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Configuration
public class TelegramBotConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(FoodStoreBot foodStoreBot) throws TelegramApiException {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(foodStoreBot);
            log.info("Telegram bot registered successfully: {}", foodStoreBot.getBotUsername());
            return botsApi;
        } catch (TelegramApiException e) {
            log.error("Failed to register Telegram bot: {}", e.getMessage(), e);
            throw e;
        }
    }
}
