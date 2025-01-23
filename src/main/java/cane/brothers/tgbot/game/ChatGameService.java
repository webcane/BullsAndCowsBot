package cane.brothers.tgbot.game;

import io.jbock.util.Either;

public interface ChatGameService {

    boolean isGameInProgress(Long chatId);

    Either<IChatGame, ChatGameException> getChatGame(Long chatId);

    Either<IChatGame, ChatGameException> newGame(Long chatId, int complexity);

    Either<IChatGame, ChatGameException> makeTurn(Long chatId, String guessMsg);

    void setLastMessageId(Long chatId, Integer messageId);


}
