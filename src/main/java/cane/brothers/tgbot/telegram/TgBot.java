package cane.brothers.tgbot.telegram;

import cane.brothers.tgbot.AppProperties;
import cane.brothers.tgbot.emoji.GameEmoji;
import cane.brothers.tgbot.game.ChatGameException;
import cane.brothers.tgbot.game.ChatGameService;
import cane.brothers.tgbot.game.ChatGameSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

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
        Long chatId = null;

        try {
            Deque<IChatCommand> commands = new LinkedList<>();
            BotApiObject data;

            // message
            if (update.hasMessage()) {
                chatId = update.getMessage().getChatId();

                var userMessage = update.getMessage();
                var userText = userMessage.getText();
                data = userMessage;

                // registered command
                if (update.getMessage().isCommand()) {
                    ICommandComposer chatCmd = ChatCommandComposer.fromString(userText);
                    if (chatCmd.isUnknown()) {
                        throw new ChatGameException(chatId, "Unrecognized message command: " + userText);
                    }

                    commands = chatCmd.getCommands();
                }

                // game message
                else if (update.getMessage().hasText()) {
                    // compose game commands
                    commands = composeGameCommands(chatId);
                }
            }

            // callbacks
            else if (update.hasCallbackQuery()) {
                var callbackQuery = update.getCallbackQuery();
                chatId = callbackQuery.getMessage().getChatId();
                var callbackData = callbackQuery.getData();
                data = callbackQuery;

                ICommandComposer chatCmd = ChatCommandComposer.fromString(callbackData);
                commands = chatCmd.getCommands();
            }

            // unknown
            else {
                data = null;
            }

            handleCommands(commands.iterator(), data);

        } catch (TelegramApiException tex) {
            log.error("Can't send message to telegram", tex);
            try {
                if (chatId != null) {
                    var errorMessage = String.format("An error occurred while processing the request.\n %s", tex.getMessage());
                    replyError(chatId, errorMessage);
                } else {
                    log.error("Unknown the update type. " + update);
                }
            } catch (TelegramApiException ex) {
                log.error("Can't send fallback message to telegram", ex);
            }

        } catch (ChatGameException cex) {
            log.error("Game exception occurred", cex);

            try {
                var errorMessage = String.format("%s %s", GameEmoji.WARN, cex.getMessage());
                replyError(cex.getChatId(), errorMessage);

            } catch (TelegramApiException exx) {
                log.error("Can't send fallback message to telegram", exx);
            }

        } catch (Exception ex) {
            log.error("Exception occurred", ex);

            if (botSettings.isDebug(chatId)) {
                try {
                    if (chatId != null) {
                        var errorMessage = String.format("%s %s", GameEmoji.WARN, ex.getMessage());
                        replyError(chatId, errorMessage);
                    } else {
                        log.error("Unknown the update type. " + update);
                    }
                } catch (TelegramApiException exx) {
                    log.error("Can't send fallback message to telegram", exx);
                }
            }
        }
    }

    @NotNull
    private LinkedList<IChatCommand> composeGameCommands(Long chatId) throws TelegramApiException, ChatGameException {
        var deq = new LinkedList<IChatCommand>();
        if (botGame.isInProgress(chatId)) {

            if (botSettings.isReplaceMessage(chatId)) {
                deq.add(GameCommand.DELETE_LAST_MESSAGE);
            }

            deq.add(GameCommand.SHOW_TURN_RESULTS);
        }

        // game not started yet or already finished
        else {
            deq.add(GameCommand.NEW_GAME_WARN);
        }
        return deq;
    }

    void handleCommands(Iterator<IChatCommand> commandsIterator, BotApiObject data) throws TelegramApiException, ChatGameException {
        while (commandsIterator.hasNext()) {
            IChatCommand command = commandsIterator.next();
            // game command
            // message/callback reply
            // game settings
            command.execute(data, botGame, botSettings, telegramClient);
        }
    }

    void replyError(Long chatId, String errorMessage) throws TelegramApiException {
        var reply = SendMessage.builder().chatId(chatId)
                .text(errorMessage)
                .build();
        telegramClient.execute(reply);
    }
}
