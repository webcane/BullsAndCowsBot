package cane.brothers.tgbot.telegram;

import cane.brothers.AbstractGuessNumber;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

public enum TgBotCommand implements ICommandMessage {
    NEW {
        @Override
        public SendMessage.SendMessageBuilder getReply(Supplier<AbstractGuessNumber> commandSupplier) {
            var secret = commandSupplier.get();
            var messageText = String.format("Enter a %d digit number%n", secret.getLength());
            return SendMessage.builder().text(messageText);
        }
    },
    INFO,
    SCORE;

    public static Optional<TgBotCommand> fromString(String str) {
        if (str == null || str.length() < 2) {
            return Optional.empty();
        }

        return Arrays.stream(TgBotCommand.values())
                .filter(command -> command.toString().equals(str))
                .findFirst();
    }

    @Override
    public String toString() {
        return String.format("/%s", name().toLowerCase());
    }

    @Override
    public SendMessage.SendMessageBuilder getReply(Supplier<AbstractGuessNumber> commandSupplier) {
        return null;
    }

}
