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
                log.info("Update settings for chat %d. Set default game complexity to: %d".formatted(chatId, complexity));
                gameSettings.setComplexity(chatId, complexity);
            } else {
                log.warn("Unknown settings: %s".formatted(callbackQuery.getData()));
            }

            if (gameService.isInProgress(chatId)) {
                CallbackSettingsCommand.CALLBACK_FINISH_GAME.execute(callbackQuery, gameService, gameSettings, telegramClient);
            }
            CallbackReplyCommand.CALLBACK_HIDE_SETTINGS.execute(callbackQuery, gameService, gameSettings, telegramClient);
            CallbackReplyCommand.CALLBACK_SETTINGS_UPDATED.execute(callbackQuery, gameService, gameSettings, telegramClient);

            if (gameService.isInProgress(chatId)) {
                CallbackGameCommand.CALLBACK_NEW_GAME_WARN.execute(callbackQuery, gameService, gameSettings, telegramClient);
            }
        }
    },
    CALLBACK_FINISH_GAME {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            var chatId = callbackQuery.getMessage().getChatId();
            log.debug("Update settings for chat %d. Finish current game".formatted(chatId));
            gameService.finishGame(chatId);
        }
    },
    CALLBACK_REPLACE_MESSAGE {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            CallbackReplyCommand.CALLBACK_ANSWER.execute(callbackQuery, gameService, gameSettings, telegramClient);
            var chatId = callbackQuery.getMessage().getChatId();
            log.info("Update settings for chat %d. Replace the guess result message".formatted(chatId));
            gameSettings.updateReplaceMessage(chatId);

            CallbackReplyCommand.CALLBACK_HIDE_SETTINGS.execute(callbackQuery, gameService, gameSettings, telegramClient);
            CallbackReplyCommand.CALLBACK_SETTINGS_UPDATED.execute(callbackQuery, gameService, gameSettings, telegramClient);
        }
    },
    CALLBACK_SHOW_TURNS {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            CallbackReplyCommand.CALLBACK_ANSWER.execute(callbackQuery, gameService, gameSettings, telegramClient);
            var chatId = callbackQuery.getMessage().getChatId();
            log.info("Update settings for chat %d. Show all turns".formatted(chatId));
            gameSettings.updateShowAllTurns(chatId);

            CallbackReplyCommand.CALLBACK_HIDE_SETTINGS.execute(callbackQuery, gameService, gameSettings, telegramClient);
            CallbackReplyCommand.CALLBACK_SETTINGS_UPDATED.execute(callbackQuery, gameService, gameSettings, telegramClient);
        }
    };

    @Override
    public String toString() {
        return String.format("/%s", name().toLowerCase());
    }

}
