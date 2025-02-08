package cane.brothers.tgbot.telegram;

import cane.brothers.tgbot.game.ChatGameException;
import cane.brothers.tgbot.game.ChatGameService;
import cane.brothers.tgbot.game.ChatGameSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
enum CallbackGameCommand implements IChatCommand<CallbackQuery> {
    CALLBACK_NEW_GAME_WARN {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            log.debug("Ask user to start new game");
            var chatId = callbackQuery.getMessage().getChatId();
            var reply = SendMessage.builder().chatId(chatId)
                    .text("Please, start another game using /new command")
                    .replyMarkup(new ReplyKeyboardRemove(true))
                    .build();

            var lastMethod = telegramClient.execute(reply);
            GameCommand.SAVE_LAST_MESSAGE.execute(lastMethod, gameService, gameSettings, telegramClient);
        }
    };

    @Override
    public String toString() {
        return String.format("/%s", name().toLowerCase());
    }
}
