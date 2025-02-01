package cane.brothers.tgbot.telegram;

import cane.brothers.tgbot.game.ChatGameException;
import cane.brothers.tgbot.game.ChatGameService;
import cane.brothers.tgbot.game.ChatGameSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Arrays;

@Slf4j
public enum ChatCommandFactory {

    NEW {
        @Override
        public IChatCommand<Message> getCommand() {
            return GameCommand.NEW;
        }
    },
    INFO {
        @Override
        public IChatCommand<Message> getCommand() {
            return ReplyCommand.INFO;
        }
    },
    SCORE {
        @Override
        public IChatCommand<Message> getCommand() {
            return GameCommand.SCORE;
        }
    },
    SETTINGS {
        @Override
        public IChatCommand<Message> getCommand() {
            return ReplyCommand.SETTINGS;
        }
    },
    UNKNOWN {
        @Override
        public IChatCommand<Message> getCommand() {
            // no commands
            return new IChatCommand<>() {
                @Override
                public void execute(Message message, ChatGameService gameService, ChatGameSettingsService
                        gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
                    var chatId = message.getChatId();
                    var warn = String.format("Unknown message command: %s", message.getText());
                    log.warn(warn);

                    if (gameSettings.isDebug(chatId)) {
                        var reply = SendMessage.builder().chatId(chatId)
                                .text(warn).build();
                        var lastMethod = telegramClient.execute(reply);
                        gameService.setLastMessageId(chatId, lastMethod.getMessageId());
                    }
                }
            };
        }
    };

    public static IChatCommand<Message> create(String message) {
        var factory = Arrays.stream(ChatCommandFactory.values())
                .filter(command -> command.toString().equals(message))
                .findFirst().orElse(ChatCommandFactory.UNKNOWN);

        return factory.getCommand();
    }

    abstract IChatCommand<Message> getCommand();

    @Override
    public String toString() {
        return "/" + name().toLowerCase();
    }
}
