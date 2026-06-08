package org.example.project.telegram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.example.project.telegram.bot.FoodStoreBot;

import java.io.File;

@Component
@RequiredArgsConstructor
public class BotMessenger {

    private final ObjectProvider<FoodStoreBot> botProvider;

    public void sendText(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage msg = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode("HTML")
                .replyMarkup(keyboard)
                .build();
        execute(msg);
    }

    public void sendText(Long chatId, String text) {
        sendText(chatId, text, null);
    }

    public void sendPhoto(Long chatId, File photo, String caption, InlineKeyboardMarkup keyboard) {
        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(photo))
                .caption(caption)
                .parseMode("HTML")
                .replyMarkup(keyboard)
                .build();
        execute(sendPhoto);
    }

    public void sendPhotoById(Long chatId, String fileId, String caption, InlineKeyboardMarkup keyboard) {
        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(fileId))
                .caption(caption)
                .parseMode("HTML")
                .replyMarkup(keyboard)
                .build();
        execute(sendPhoto);
    }

    public void editText(Long chatId, Integer messageId, String text, InlineKeyboardMarkup keyboard) {
        EditMessageText edit = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(text)
                .parseMode("HTML")
                .replyMarkup(keyboard)
                .build();
        execute(edit);
    }

    public void sendReplyKeyboard(Long chatId, String text, ReplyKeyboardMarkup keyboard) {
        SendMessage msg = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode("HTML")
                .replyMarkup(keyboard)
                .build();
        execute(msg);
    }

    public void removeReplyKeyboard(Long chatId, String text) {
        ReplyKeyboardRemove remove = new ReplyKeyboardRemove(true);
        SendMessage msg = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode("HTML")
                .replyMarkup(remove)
                .build();
        execute(msg);
    }

    private void execute(Object method) {
        try {
            FoodStoreBot bot = botProvider.getIfAvailable();
            if (bot == null) {
                throw new org.example.project.telegram.exception.TelegramBotException("Telegram bot is not configured or is disabled.");
            }
            if (method instanceof SendMessage sm) bot.execute(sm);
            else if (method instanceof SendPhoto sp) bot.execute(sp);
            else if (method instanceof EditMessageText em) bot.execute(em);
        } catch (TelegramApiException e) {
            if (e.getMessage() != null && e.getMessage().contains("message is not modified")) {
                return;
            }
            throw new org.example.project.telegram.exception.TelegramBotException("Telegram API error: " + e.getMessage(), e);
        }
    }
}
