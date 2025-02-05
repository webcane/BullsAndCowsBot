package cane.brothers.tgbot.telegram;

import cane.brothers.tgbot.game.ChatGameException;
import cane.brothers.tgbot.game.ChatGameService;
import cane.brothers.tgbot.game.ChatGameSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Arrays;

@Slf4j
enum ChatCallbackCommandFactory {
    COMPLEXITY {
        @Override
        public IChatCommand<CallbackQuery> getCommand() {
            return CallbackSettingsCommand.CALLBACK_COMPLEXITY;
        }
    },
    MENU_SETTINGS {
        @Override
        public IChatCommand<CallbackQuery> getCommand() {
            return CallbackReplyCommand.CALLBACK_SETTINGS;
        }
    },
    MENU_COMPLEXITY {
        @Override
        public IChatCommand<CallbackQuery> getCommand() {
            return CallbackReplyCommand.CALLBACK_COMPLEXITY;
        }
    },
    MENU_REPLACE_MESSAGE {
        @Override
        public IChatCommand<CallbackQuery> getCommand() {
            return CallbackSettingsCommand.CALLBACK_REPLACE_MESSAGE;
        }
    },
    MENU_SHOW_TURNS {
        @Override
        public IChatCommand<CallbackQuery> getCommand() {
            return CallbackSettingsCommand.CALLBACK_SHOW_TURNS;
        }
    },
    MENU_HIDE_SETTINGS {
        @Override
        public IChatCommand<CallbackQuery> getCommand() {
            return CallbackReplyCommand.CALLBACK_HIDE_SETTINGS;
        }
    },
    UNKNOWN {
        @Override
        public IChatCommand<CallbackQuery> getCommand() {
            return new IChatCommand<>() {
                @Override
                public void execute(CallbackQuery callback, ChatGameService gameService, ChatGameSettingsService
                        gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
                    var chatId = callback.getMessage().getChatId();
                    var warn = String.format("Unknown callback command: %s", callback.getData());
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

    public static IChatCommand<CallbackQuery> create(String message) {
        var factory = Arrays.stream(ChatCallbackCommandFactory.values())
                .filter(command -> command.toString().equals(getMessageCommand(message)))
                .findFirst().orElse(ChatCallbackCommandFactory.UNKNOWN);

        return factory.getCommand();
    }

    private static String getMessageCommand(String message) {
        var msg = message.split("=");
        return msg[0];
    }

    abstract IChatCommand<CallbackQuery> getCommand();

    @Override
    public String toString() {
        return "/" + name().toLowerCase();
    }
}
