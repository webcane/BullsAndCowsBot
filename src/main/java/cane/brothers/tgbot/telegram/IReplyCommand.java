package cane.brothers.tgbot.telegram;

import cane.brothers.tgbot.game.ChatGameException;
import cane.brothers.tgbot.game.ChatGameService;
import cane.brothers.tgbot.game.ChatGameSettingsService;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

public interface IReplyCommand extends IChatCommand {

    default void execute(BotApiObject data, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
        if (data instanceof Message message) {
            execute(message, telegramClient);
        } else if (data instanceof CallbackQuery callback) {
            execute(callback, telegramClient);
        }
    }

    default void execute(Message message, TelegramClient telegramClient) throws TelegramApiException {
    }

    default void execute(CallbackQuery callbackQuery, TelegramClient telegramClient) throws TelegramApiException {
    }

    default InlineKeyboardMarkup getSettingsKeyboardMarkup() {
        var complexityButton = InlineKeyboardButton.builder().text("Complexity")
                .callbackData(ChatCommandComposer.MENU_COMPLEXITY.toString()).build();

        var resultsButton = InlineKeyboardButton.builder().text("Results")
                .callbackData(ChatCommandComposer.MENU_REPLACE_MESSAGE.toString()).build();

        var turnsButton = InlineKeyboardButton.builder().text("Turns")
                .callbackData(ChatCommandComposer.MENU_SHOW_TURNS.toString()).build();

        var hideButton = InlineKeyboardButton.builder().text("Hide")
                .callbackData(ChatCommandComposer.MENU_HIDE_SETTINGS.toString()).build();

        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(List.of(complexityButton, resultsButton, turnsButton)))
                .keyboardRow(new InlineKeyboardRow(hideButton))
                .build();
    }
}
