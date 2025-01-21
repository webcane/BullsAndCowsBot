package cane.brothers.tgbot.telegram;

import cane.brothers.game.IGuessTurn;
import cane.brothers.tgbot.emoji.GameEmoji;
import cane.brothers.tgbot.game.ChatGameException;
import io.jbock.util.Either;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

enum TurnCommand implements ITurnCommand {
    SHOW_RESULT {
        @Override
        public SendMessage.SendMessageBuilder<?,?> getReply(Supplier<Either<IGuessTurn, ChatGameException>> commandSupplier) {
            AtomicReference<SendMessage.SendMessageBuilder<?,?>> reply = new AtomicReference<>();
            commandSupplier.get().ifLeftOrElse(
                    guessTurn -> reply.set(SendMessage.builder().text(displayEmojiResult(guessTurn))),
                    r -> reply.set(SendMessage.builder().text(r.getMessage())));
            return reply.get();
        }

        public String displayEmojiResult(IGuessTurn gameTurn) {
            return gameTurn.isWin() ? "" + GameEmoji.HIT:
                    "" + GameEmoji.getDigit(gameTurn.getBulls()) + GameEmoji.BULL +
                    GameEmoji.getDigit(gameTurn.getCows()) + GameEmoji.COW;
        }
    }
}
