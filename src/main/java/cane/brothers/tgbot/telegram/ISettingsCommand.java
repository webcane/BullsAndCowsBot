package cane.brothers.tgbot.telegram;

import cane.brothers.tgbot.game.ChatGameException;
import cane.brothers.tgbot.game.ChatGameService;
import cane.brothers.tgbot.game.ChatGameSettingsService;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


public interface ISettingsCommand extends IChatCommand {

    default void execute(BotApiObject data, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
        if (data instanceof Message message) {
            execute(message, gameService);
        } else if (data instanceof CallbackQuery callback) {
            execute(callback, gameService, gameSettings);
        }
    }

    default void execute(Message message, ChatGameService gameService) throws ChatGameException {
    }

    default void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings) throws ChatGameException {
    }
}
