package cane.brothers.tgbot.telegram;

import cane.brothers.tgbot.AppProperties;
import cane.brothers.tgbot.emoji.GameEmoji;
import cane.brothers.tgbot.game.ChatGameException;
import cane.brothers.tgbot.game.ChatGameService;
import cane.brothers.tgbot.game.ChatGameSettingsService;
import io.jbock.util.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.Serializable;

@Slf4j
@Component
@RequiredArgsConstructor
public class TgBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final AppProperties properties;
    private final ChatGameService botGame;
    private final ChatGameSettingsService botSettings;

    @Override
    public String getBotToken() {
        return properties.token();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        log.info("Registered TgBot. Running state is: {}", botSession.isRunning());
    }

    @Override
    public void consume(Update update) {
        // message
        if (update.hasMessage()) {
            Long chatId = update.getMessage().getChatId();
            var userMessage = update.getMessage().getText();

            try {
                // keyboard menu
                // TODO
                //         // action command
                //        else if (GameCommand.REPLACE_MESSAGE == command) {
                //            commandReply = command.getReply(() -> botSettings.updateReplaceMessage(chatId));
                //        }

                // registered command
                if (update.getMessage().isCommand()) {
                    GameCommand command = GameCommand.fromString(userMessage);
                    var lastMethod = replyCommand(chatId, command, userMessage);
                    saveGameMessage(chatId, lastMethod);
                }

                // guess message
                else if (update.getMessage().hasText()) {
                    if (botGame.isGameInProgress(chatId)) {
                        updateGameMessage(chatId);

                        var lastMethod = replyCommand(chatId, GameCommand.SHOW_ALL_TURNS_RESULT, userMessage);
                        saveGameMessage(chatId, lastMethod);

                        // show win
                        if (!botGame.isGameInProgress(chatId)) {
                            replyCommand(chatId, GameCommand.SHOW_WIN_RESULT, userMessage);
                            replyCommand(chatId, GameCommand.SHOW_WIN_MESSAGE, userMessage);
                        }
                    } else {
                        var lastMethod = replyCommand(chatId, GameCommand.NEW_GAME_WARN, userMessage);
                        saveGameMessage(chatId, lastMethod);
                    }
                }
            } catch (TelegramApiException tex) {
                log.error("Can't send message to telegram", tex);
                try {
                    var msg = String.format("An error occurred while processing the request.\n %s", tex.getMessage());
                    replyCommand(chatId, GameCommand.UNKNOWN, msg);
                } catch (TelegramApiException ex) {
                    log.error("Can't send fallback message to telegram", ex);
                }

            } catch (Exception ex) {
                log.error("Exception occurred", ex);

                if (botSettings.isDebug(chatId)) {
                    try {
                        var errorMessage = String.format("%s %s", GameEmoji.WARN, ex.getMessage());
                        replyCommand(chatId, GameCommand.UNKNOWN, errorMessage);
                    } catch (TelegramApiException exx) {
                        log.error("Can't send fallback message to telegram", exx);
                    }
                }
            }
        }
    }

    private void updateGameMessage(Long chatId) throws TelegramApiException {
        if (botSettings.isReplaceMessage(chatId)) {
            replyCommand(chatId, GameCommand.DELETE, null);
        }
    }

    private void saveGameMessage(Long chatId, Serializable lastMethod) {
        if (lastMethod instanceof Message msg) {
            botGame.setLastMessageId(chatId, msg.getMessageId());
        }
    }


    protected Serializable replyCommand(Long chatId, IGameCommand command, String userMessage) throws TelegramApiException {
        BotApiMethod<?> commandReply;

        // start new game
        if (GameCommand.NEW == command) {
            var complexity = botSettings.getComplexity(chatId);
            commandReply = command.getReply(() -> botGame.newGame(chatId, complexity));
        }
        // make guess turn
        else if (GameCommand.SHOW_ALL_TURNS_RESULT == command) {
            commandReply = command.getReply(() -> botGame.makeTurn(chatId, userMessage));
        }
        // unknown command || fallback message
        else if (command.isUnknown()) {
            commandReply = command.getReply(() ->
                    Either.right(new ChatGameException(chatId, userMessage)));
        }
        // all other known commands
        else {
            commandReply = command.getReply(() -> botGame.getChatGame(chatId));
        }

        // send message if have any
        return commandReply == null ? null : telegramClient.execute(commandReply);
    }

    // TODO do command
}
