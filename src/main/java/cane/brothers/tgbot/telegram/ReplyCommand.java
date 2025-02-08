package cane.brothers.tgbot.telegram;

import cane.brothers.tgbot.game.ChatGameService;
import cane.brothers.tgbot.game.ChatGameSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Slf4j
enum ReplyCommand implements IChatCommand<Message>, Utils {
    INFO {
        @Override
        public void execute(Message message, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException {
            var chatId = message.getChatId();
            log.debug("Show game rules for chat %d".formatted(chatId));

            var reply = SendMessage.builder().chatId(chatId)
                    .parseMode(ParseMode.MARKDOWNV2)
                    .text(escape(readMarkDownFile("rules.tg.md")))
                    .build();
            telegramClient.execute(reply);
        }
    },
    SETTINGS {
        // replace_message замещать последнее сообщение
        @Override
        public void execute(Message message, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException {
            var chatId = message.getChatId();
            log.debug("Show settings for chat %d".formatted(chatId));

            var reply = SendMessage.builder().text("Bulls & Cows settings:").chatId(chatId)
                    .replyMarkup(getSettingsKeyboardMarkup())
                    .build();
            telegramClient.execute(reply);
        }

        InlineKeyboardMarkup getSettingsKeyboardMarkup() {
            var complexityButton = InlineKeyboardButton.builder().text("Change complexity")
                    .callbackData(ChatCallbackCommandFactory.MENU_COMPLEXITY.toString()).build();

            var resultsButton = InlineKeyboardButton.builder().text("Show all results")
                    .callbackData(ChatCallbackCommandFactory.MENU_REPLACE_MESSAGE.toString()).build();

            var turnsButton = InlineKeyboardButton.builder().text("Show all turns")
                    .callbackData(ChatCallbackCommandFactory.MENU_SHOW_TURNS.toString()).build();

            var hideButton = InlineKeyboardButton.builder().text("Hide settings")
                    .callbackData(ChatCallbackCommandFactory.MENU_HIDE_SETTINGS.toString()).build();

            return InlineKeyboardMarkup.builder()
                    .keyboardRow(new InlineKeyboardRow(List.of(complexityButton, resultsButton, turnsButton)))
                    .keyboardRow(new InlineKeyboardRow(hideButton))
                    .build();
        }
    };

    @Override
    public String toString() {
        return String.format("/%s", name().toLowerCase());
    }
}
