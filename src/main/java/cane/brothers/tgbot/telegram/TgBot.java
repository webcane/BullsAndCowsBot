package cane.brothers.tgbot.telegram;

import cane.brothers.tgbot.AppProperties;
import cane.brothers.tgbot.emoji.GameEmoji;
import cane.brothers.tgbot.game.ChatGameException;
import cane.brothers.tgbot.game.ChatGameService;
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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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


    private boolean debug = true;

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
                // command
                if (update.getMessage().isCommand()) {
                    GameCommand command = GameCommand.fromString(userMessage);

                    // action command
                    if (GameCommand.REPLACE_MESSAGE == command) {
                        botGame.updateReplaceMessage(chatId);
                    }
                    // message command
                    else {
                        BotApiMethod<?> commandReply = getCommandReply(chatId, userMessage);
                        var lastMethod = sendMessage(commandReply);

                        saveGameMessage(chatId, lastMethod);
                    }
                }

                // guess
                else if (update.getMessage().hasText()) {

                    if (botGame.isGameStarted(chatId)) {
                        updateGameMessage(chatId);

                        var turnReplyBuilder = TurnCommand.SHOW_RESULT.getReply(() -> botGame.makeTurn(chatId, userMessage));
                        var lastMethod = sendMessage(turnReplyBuilder.chatId(chatId).build());

                        saveGameMessage(chatId, lastMethod);
                    }
                    else {
                        var commandReply = SendMessage.builder().text("Please, start new game first!").chatId(chatId).build();
                        sendMessage(commandReply);
                    }
                }
            } catch (TelegramApiException tex) {
                log.error("Can't send message to telegram", tex);
                try {
                    var msg = String.format("An error occurred while processing the request.\n %s", tex.getMessage());
                    SendMessage fallbackMessage = SendMessage.builder().chatId(chatId).text(msg).build();

                    sendMessage(fallbackMessage);
                } catch (TelegramApiException ex) {
                    log.error("Can't send fallback message to telegram", ex);
                }

            } catch (Exception ex) {
                log.error("Exception occurred", ex);

                if (debug) {
                    var errorMessage = String.format("%s %s", GameEmoji.WARN, ex.getMessage());
                    var errorReply = SendMessage.builder().text(errorMessage).chatId(chatId).build();

                    try {
                        sendMessage(errorReply);
                    } catch (TelegramApiException exx) {
                        log.error("Can't send fallback message to telegram", exx);
                    }
                }
            }
        }
    }

    private void updateGameMessage(Long chatId) throws TelegramApiException {
        if (botGame.isReplaceMessage(chatId)) {

            var messageBuilder = GameCommand.DELETE.getReply(() -> botGame.getChatGame(chatId));
            sendMessage(messageBuilder.build());
        }
    }

    private void saveGameMessage(Long chatId, Serializable lastMethod) {
        if (botGame.isReplaceMessage(chatId)) {

            if (lastMethod instanceof Message msg) {
                botGame.setLastMessageId(chatId, msg.getMessageId());
            }
        }
    }


    protected BotApiMethod<? extends Serializable> getCommandReply(Long chatId, String userMessage) {

        GameCommand command = GameCommand.fromString(userMessage);

        if (GameCommand.NEW == command) {
            // TODO game complexity
            return command.getReply(() -> botGame.newGame(chatId, 4))
                    .build();
        } else if (GameCommand.DELETE == command) {
            return command.getReply(() -> botGame.getChatGame(chatId))
                    .build();
        } else {
            return GameCommand.UNKNOWN.getReply(() ->
                            Either.right(new ChatGameException(chatId, "unknown command " + userMessage)))
                    .build();
        }
    }

    protected Serializable sendMessage(BotApiMethod<? extends Serializable> sendMessage) throws TelegramApiException {
        return telegramClient.execute(sendMessage);
    }

}
