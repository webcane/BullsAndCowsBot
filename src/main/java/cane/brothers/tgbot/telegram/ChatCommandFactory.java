package cane.brothers.tgbot.telegram;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.Arrays;

@Slf4j
@Getter
@RequiredArgsConstructor
enum ChatCommandFactory implements ICommandFactory<Message> {
    START(GameCommand.START),
    INFO(ReplyCommand.INFO),
    SCORE(GameCommand.SCORE),
    SETTINGS(ReplyCommand.SETTINGS),
    UNKNOWN((message, gameService, gameSettings, telegramClient) -> {
        var chatId = message.getChatId();
        var warn = String.format("Unknown message command: %s", message.getText());
        log.warn(warn);

        if (gameSettings.isDebug(chatId)) {
            var reply = SendMessage.builder().chatId(chatId)
                    .text(warn).build();
            var lastMethod = telegramClient.execute(reply);
            gameService.setLastMessageId(chatId, lastMethod.getMessageId());
        }
    });

    private final IChatCommand<Message> command;


    public static IChatCommand<Message> create(String message) {
        var factory = Arrays.stream(ChatCommandFactory.values())
                .filter(command -> command.toString().equals(message))
                .findFirst().orElse(ChatCommandFactory.UNKNOWN);

        return factory.getCommand();
    }

    @Override
    public String toString() {
        return "/" + name().toLowerCase();
    }
}
