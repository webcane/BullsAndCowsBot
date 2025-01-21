package cane.brothers.tgbot.telegram;

import cane.brothers.game.IGuessTurn;
import cane.brothers.tgbot.game.ChatGameException;
import io.jbock.util.Either;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.function.Supplier;

interface ITurnCommand {

    SendMessage.SendMessageBuilder<?,?> getReply(Supplier<Either<IGuessTurn, ChatGameException>> commandSupplier);
}
