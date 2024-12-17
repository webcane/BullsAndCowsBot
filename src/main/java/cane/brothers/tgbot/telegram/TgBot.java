package cane.brothers.tgbot.telegram;

import cane.brothers.GameNumber;
import cane.brothers.GuessNumber;
import cane.brothers.GuessResult;
import cane.brothers.tgbot.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class TgBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private static final int secretLength = 4;
    private final TelegramClient telegramClient;
    private final AppProperties properties;
    private final TgBotGame chatGame;

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

            // command
            if (update.getMessage().isCommand()) {
                SendMessage commandReply = getCommandReply(chatId, userMessage);
                sendMessage(commandReply);
            }

            // guess
            else if (update.getMessage().hasText()) {
                GuessNumber guess = new GuessNumber(userMessage, secretLength);
                String guessMessage = "Try again";
                if (guess.isValid()) {
                    GameNumber secret = chatGame.getSecret(chatId);
                    GuessResult result = secret.match(guess);
                    guessMessage = result.toString();
                }
                SendMessage guessReply = SendMessage.builder().text(guessMessage).chatId(chatId).build();
                sendMessage(guessReply);
            }
        }
    }

    protected SendMessage getCommandReply(Long chatId, String userMessage) {
        AtomicReference<SendMessage> reply = new AtomicReference<>();
        TgBotCommand.fromString(userMessage).ifPresentOrElse(command -> {
                    // return related command message
                    if (TgBotCommand.NEW == command) {
                        var messageBuilder = command.getReply(() -> chatGame.newGame(chatId));
                        reply.set(messageBuilder.chatId(chatId).build());
                    }
                },
                () -> {
                    var fallbackMessage = String.format("unknown command %s", userMessage);
                    log.warn(fallbackMessage);
                    var fallbackReply = SendMessage.builder().text(fallbackMessage).chatId(chatId).build();
                    reply.set(fallbackReply);
                });
        return reply.get();
    }

    protected void sendMessage(SendMessage sendMessage) {
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Can't send message to telegram", e);
            try {
                var msg = String.format("An error occurred while processing the request.\n %s", e.getMessage());
                SendMessage fallbackMessage = SendMessage.builder()
                        .chatId(sendMessage.getChatId())
                        .text(msg).build();
                telegramClient.execute(fallbackMessage);
            } catch (TelegramApiException ex) {
                log.error("Can't send fallback message to telegram", ex);
            }
        }
    }
}
