package cane.brothers.tgbot.game;

import cane.brothers.game.IGuessTurn;
import io.jbock.util.Either;

public interface ChatGameService {


    boolean isGameStarted(Long chatId);

    Either<IChatGame, ChatGameException> getChatGame(Long chatId);

    Either<IChatGame, ChatGameException> newGame(Long chatId, int complexity);

    Either<IGuessTurn, ChatGameException> makeTurn(Long chatId, String guessMsg);

    void setLastMessageId(Long chatId, Integer messageId);
}
