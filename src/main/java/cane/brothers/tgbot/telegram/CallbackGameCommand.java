package cane.brothers.tgbot.telegram;

import cane.brothers.tgbot.game.ChatGameException;
import cane.brothers.tgbot.game.ChatGameService;
import cane.brothers.tgbot.game.ChatGameSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
enum CallbackGameCommand implements IChatCommand<CallbackQuery> {
    CALLBACK_NEW_GAME_WARN {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            var chatId = callbackQuery.getMessage().getChatId();
            var reply = SendMessage.builder().chatId(chatId)
                    .text("Please, start another game using /new command").build();

            var lastMethod = telegramClient.execute(reply);
            gameService.setLastMessageId(chatId, lastMethod.getMessageId());
        }
    };

    @Override
    public String toString() {
        return String.format("/%s", name().toLowerCase());
    }
}
