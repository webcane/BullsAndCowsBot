package cane.brothers.tgbot.telegram;

import cane.brothers.tgbot.game.ChatGameException;
import cane.brothers.tgbot.game.ChatGameService;
import cane.brothers.tgbot.game.ChatGameSettingsService;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public interface IChatCommand {

    void execute(BotApiObject data, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException;
}
