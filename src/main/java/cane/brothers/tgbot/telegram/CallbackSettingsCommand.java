package cane.brothers.tgbot.telegram;

import cane.brothers.tgbot.game.ChatGameException;
import cane.brothers.tgbot.game.ChatGameService;
import cane.brothers.tgbot.game.ChatGameSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
enum CallbackSettingsCommand implements IChatCommand<CallbackQuery> {
    CALLBACK_COMPLEXITY {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            var msg = callbackQuery.getData().split("=");
            var chatId = callbackQuery.getMessage().getChatId();

            if (msg.length > 1) {
                var complexity = Integer.valueOf(msg[1]);
                log.info("set default game complexity to: " + complexity);
                gameSettings.setComplexity(chatId, complexity);
            } else {
                log.info("unknown settings: " + callbackQuery.getData());
            }

            if (gameService.isInProgress(chatId)) {
                CallbackSettingsCommand.CALLBACK_FINISH_GAME.execute(callbackQuery, gameService, gameSettings, telegramClient);
            }
            CallbackReplyCommand.CALLBACK_HIDE_SETTINGS.execute(callbackQuery, gameService, gameSettings, telegramClient);
            CallbackReplyCommand.CALLBACK_SETTINGS_UPDATED.execute(callbackQuery, gameService, gameSettings, telegramClient);
            CallbackGameCommand.CALLBACK_NEW_GAME_WARN.execute(callbackQuery, gameService, gameSettings, telegramClient);
        }
    },
    CALLBACK_FINISH_GAME {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            // TODO logging
            gameService.finishGame(callbackQuery.getMessage().getChatId());
        }
    },
    CALLBACK_REPLACE_MESSAGE {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            CallbackReplyCommand.CALLBACK_ANSWER.execute(callbackQuery, gameService, gameSettings, telegramClient);

            gameSettings.updateReplaceMessage(callbackQuery.getMessage().getChatId());

            CallbackReplyCommand.CALLBACK_HIDE_SETTINGS.execute(callbackQuery, gameService, gameSettings, telegramClient);
            CallbackReplyCommand.CALLBACK_SETTINGS_UPDATED.execute(callbackQuery, gameService, gameSettings, telegramClient);
        }
    },
    CALLBACK_SHOW_TURNS {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            CallbackReplyCommand.CALLBACK_ANSWER.execute(callbackQuery, gameService, gameSettings, telegramClient);

            gameSettings.updateShowAllTurns(callbackQuery.getMessage().getChatId());

            CallbackReplyCommand.CALLBACK_HIDE_SETTINGS.execute(callbackQuery, gameService, gameSettings, telegramClient);
            CallbackReplyCommand.CALLBACK_SETTINGS_UPDATED.execute(callbackQuery, gameService, gameSettings, telegramClient);
        }
    };

    @Override
    public String toString() {
        return String.format("/%s", name().toLowerCase());
    }

}
