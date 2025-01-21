package cane.brothers.tgbot.telegram;

import cane.brothers.tgbot.game.ChatGameException;
import cane.brothers.tgbot.game.IChatGame;
import io.jbock.util.Either;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;

import java.util.function.Supplier;

interface IGameCommand {

    BotApiMethod.BotApiMethodBuilder<?,?,?> getReply(Supplier<Either<IChatGame, ChatGameException>> commandSupplier);

    boolean isUnknown();

}
