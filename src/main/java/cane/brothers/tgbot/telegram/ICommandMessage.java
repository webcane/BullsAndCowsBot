package cane.brothers.tgbot.telegram;

import cane.brothers.AbstractGuessNumber;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.function.Supplier;

public interface ICommandMessage {

    SendMessage.SendMessageBuilder getReply(Supplier<AbstractGuessNumber> commandSupplier);

}
