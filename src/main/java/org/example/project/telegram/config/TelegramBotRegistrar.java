package org.example.project.telegram.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project.telegram.bot.FoodStoreBot;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
@ConditionalOnExpression("${telegram.bot.enabled:true} and '${telegram.bot.token:}'!='' and '${telegram.bot.username:}'!=''")
@RequiredArgsConstructor
@Slf4j
public class TelegramBotRegistrar implements ApplicationListener<ApplicationReadyEvent> {

    private final TelegramBotsApi telegramBotsApi;
    private final FoodStoreBot foodStoreBot;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            telegramBotsApi.registerBot(foodStoreBot);
            log.info("Telegram bot registered successfully: {}", foodStoreBot.getBotUsername());
            registerCommands();
        } catch (TelegramApiException e) {
            if (e.getMessage() != null && e.getMessage().contains("409")) {
                log.error("Telegram bot 409 Conflict: another instance is already running. " +
                        "Stop all other running instances of this application first.", e);
            } else {
                log.warn("Failed to register Telegram bot (will continue without bot): {}", e.getMessage());
            }
        } catch (Exception ex) {
            log.error("Unexpected error during Telegram bot registration: {}", ex.getMessage(), ex);
        }
    }

    private void registerCommands() {
        try {
            foodStoreBot.execute(SetMyCommands.builder()
                    .commands(List.of(
                            BotCommand.builder().command("start").description("Botni boshlash").build(),
                            BotCommand.builder().command("help").description("Yordam").build(),
                            BotCommand.builder().command("menu").description("Asosiy menyu").build(),
                            BotCommand.builder().command("orders").description("Buyurtmalarim").build(),
                            BotCommand.builder().command("cart").description("Savatim").build(),
                            BotCommand.builder().command("cancel").description("Bekor qilish").build()
                    ))
                    .build());
            log.info("Bot commands registered");
        } catch (TelegramApiException e) {
            log.warn("Failed to register bot commands: {}", e.getMessage());
        }
    }
}
