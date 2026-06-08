package org.example.project.telegram.bot;

import lombok.extern.slf4j.Slf4j;
import org.example.project.telegram.config.TelegramBotProperties;
import org.example.project.telegram.handler.BotUpdateHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@ConditionalOnExpression("${telegram.bot.enabled:true} and '${telegram.bot.token:}'!='' and '${telegram.bot.username:}'!=''")
@Slf4j
public class FoodStoreBot extends TelegramLongPollingBot {

    private final TelegramBotProperties properties;
    private final BotUpdateHandler updateHandler;
    private final ConfigurableApplicationContext applicationContext;

    public FoodStoreBot(TelegramBotProperties properties, BotUpdateHandler updateHandler,
                        ConfigurableApplicationContext applicationContext) {
        super(properties.getToken());
        this.properties = properties;
        this.updateHandler = updateHandler;
        this.applicationContext = applicationContext;
    }

    @Override
    public String getBotUsername() {
        return properties.getUsername();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!applicationContext.isActive()) {
            log.warn("Dropping update {} — application context is not active yet", update.getUpdateId());
            return;
        }
        log.debug("Update received: {}", update.getUpdateId());
        updateHandler.handle(update);
    }
}
