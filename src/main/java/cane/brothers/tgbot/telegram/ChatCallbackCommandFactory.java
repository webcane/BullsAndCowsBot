package cane.brothers.tgbot.telegram;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Arrays;

@Slf4j
@Getter
@RequiredArgsConstructor
enum ChatCallbackCommandFactory implements ICommandFactory<CallbackQuery> {
    COMPLEXITY(CallbackSettingsCommand.CALLBACK_COMPLEXITY),
    MENU_SETTINGS(CallbackReplyCommand.CALLBACK_SETTINGS),
    MENU_COMPLEXITY(CallbackReplyCommand.CALLBACK_COMPLEXITY),
    MENU_REPLACE_MESSAGE(CallbackSettingsCommand.CALLBACK_REPLACE_MESSAGE),
    MENU_SHOW_TURNS(CallbackSettingsCommand.CALLBACK_SHOW_TURNS),
    MENU_HIDE_SETTINGS(CallbackReplyCommand.CALLBACK_HIDE_SETTINGS),
    UNKNOWN((callback, gameService, gameSettings, telegramClient) -> {
        var chatId = callback.getMessage().getChatId();
        var warn = String.format("Unknown callback command: %s", callback.getData());
        log.warn(warn);

        if (gameSettings.isDebug(chatId)) {
            var reply = SendMessage.builder().chatId(chatId)
                    .text(warn).build();
            var lastMethod = telegramClient.execute(reply);
            gameService.setLastMessageId(chatId, lastMethod.getMessageId());
        }
    });

    private final IChatCommand<CallbackQuery> command;

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

    @Override
    public String toString() {
        return "/" + name().toLowerCase();
    }
}
