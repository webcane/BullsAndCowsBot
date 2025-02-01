package cane.brothers.tgbot.telegram;

import cane.brothers.tgbot.game.ChatGameException;
import cane.brothers.tgbot.game.ChatGameService;
import cane.brothers.tgbot.game.ChatGameSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
enum CallbackReplyCommand implements IChatCommand<CallbackQuery> {

    CALLBACK_ANSWER {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            var reply = AnswerCallbackQuery.builder().callbackQueryId(callbackQuery.getId())
                    .cacheTime(3600) // 1h
                    .build();
            telegramClient.execute(reply);
        }
    },
    CALLBACK_COMPLEXITY {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            CallbackReplyCommand.CALLBACK_ANSWER.execute(callbackQuery, gameService, gameSettings, telegramClient);
            CallbackReplyCommand.CALLBACK_COMPLEXITY_TEXT.execute(callbackQuery, gameService, gameSettings, telegramClient);
            CallbackReplyCommand.CALLBACK_COMPLEXITY_REPLY_MARKUP.execute(callbackQuery, gameService, gameSettings, telegramClient);
        }
    },
    CALLBACK_COMPLEXITY_TEXT {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            var reply = EditMessageText.builder().chatId(callbackQuery.getMessage().getChatId())
                    .parseMode("HTML")
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .text("Choose game <i>complexity</i>:")
                    .build();
            telegramClient.execute(reply);
        }
    },
    CALLBACK_COMPLEXITY_REPLY_MARKUP {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            var reply = EditMessageReplyMarkup.builder().chatId(callbackQuery.getMessage().getChatId())
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .replyMarkup(getKeyboardMarkup()).build();
            telegramClient.execute(reply);
        }

        InlineKeyboardMarkup getKeyboardMarkup() {
            List<InlineKeyboardButton> inlineButtons = new ArrayList<>();
            for (String c : List.of("1", "2", "3", "4", "5", "6")) {
                var data = String.format("%s=%s", ChatCallbackCommandFactory.COMPLEXITY, c);
                inlineButtons.add(InlineKeyboardButton.builder().text(c).callbackData(data).build());
            }
            return InlineKeyboardMarkup.builder()
                    .keyboardRow(new InlineKeyboardRow(inlineButtons))
                    .keyboardRow(new InlineKeyboardRow(InlineKeyboardButton.builder().text("<< Back to Settings")
                            .callbackData(ChatCallbackCommandFactory.MENU_SETTINGS.toString()).build()))
                    .build();
        }
    },
    CALLBACK_SETTINGS_REPLY_MARKUP {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            var reply = EditMessageReplyMarkup.builder().chatId(callbackQuery.getMessage().getChatId())
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .replyMarkup(getSettingsKeyboardMarkup()).build();
            telegramClient.execute(reply);
        }

        InlineKeyboardMarkup getSettingsKeyboardMarkup() {
            var complexityButton = InlineKeyboardButton.builder().text("Complexity")
                    .callbackData(ChatCallbackCommandFactory.MENU_COMPLEXITY.toString()).build();

            var resultsButton = InlineKeyboardButton.builder().text("Results")
                    .callbackData(ChatCallbackCommandFactory.MENU_REPLACE_MESSAGE.toString()).build();

            var turnsButton = InlineKeyboardButton.builder().text("Turns")
                    .callbackData(ChatCallbackCommandFactory.MENU_SHOW_TURNS.toString()).build();

            var hideButton = InlineKeyboardButton.builder().text("Hide")
                    .callbackData(ChatCallbackCommandFactory.MENU_HIDE_SETTINGS.toString()).build();

            return InlineKeyboardMarkup.builder()
                    .keyboardRow(new InlineKeyboardRow(List.of(complexityButton, resultsButton, turnsButton)))
                    .keyboardRow(new InlineKeyboardRow(hideButton))
                    .build();
        }
    },
    CALLBACK_SETTINGS {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            CallbackReplyCommand.CALLBACK_ANSWER.execute(callbackQuery, gameService, gameSettings, telegramClient);
            CallbackReplyCommand.CALLBACK_SETTINGS_TEXT.execute(callbackQuery, gameService, gameSettings, telegramClient);
            CallbackReplyCommand.CALLBACK_SETTINGS_REPLY_MARKUP.execute(callbackQuery, gameService, gameSettings, telegramClient);
        }
    }, CALLBACK_SETTINGS_TEXT {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            var reply = EditMessageText.builder().chatId(callbackQuery.getMessage().getChatId())
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .text("Bulls & Cows settings:")
                    .build();
            telegramClient.execute(reply);
        }
    },
    CALLBACK_HIDE_SETTINGS {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            // TODO
            CallbackReplyCommand.CALLBACK_ANSWER.execute(callbackQuery, gameService, gameSettings, telegramClient);

            var reply = DeleteMessage.builder().chatId(callbackQuery.getMessage().getChatId())
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .build();
            telegramClient.execute(reply);
        }
    },

    CALLBACK_SETTINGS_UPDATED {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            var reply = SendMessage.builder().chatId(callbackQuery.getMessage().getChatId())
                    .text("Settings were updated")
                    .build();
            telegramClient.execute(reply);
        }
    };

    @Override
    public String toString() {
        return String.format("/%s", name().toLowerCase());
    }
}
