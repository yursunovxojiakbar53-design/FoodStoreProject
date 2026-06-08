package org.example.project.telegram.config;

import lombok.RequiredArgsConstructor;
import org.example.project.telegram.bot.FoodStoreBot;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@RequiredArgsConstructor
@ConditionalOnExpression("${telegram.bot.enabled:true} and '${telegram.bot.token:}'!='' and '${telegram.bot.username:}'!=''")
public class TelegramBotConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(FoodStoreBot bot) throws TelegramApiException {
        // Create TelegramBotsApi instance but do NOT register the bot here.
        // Registration is deferred until the application is fully started
        // to avoid update-processing starting before the Spring context
        // is fully refreshed (which can cause configuration binding errors
        // and 409 GetUpdates conflicts).
        return new TelegramBotsApi(DefaultBotSession.class);
    }
}
