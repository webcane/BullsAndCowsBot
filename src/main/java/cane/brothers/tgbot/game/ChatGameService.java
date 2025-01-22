package cane.brothers.tgbot.game;

import io.jbock.util.Either;

public interface ChatGameService {

    boolean isGameInProgress(Long chatId);

    boolean isReplaceMessage(Long chatId);

    boolean isDebug(Long chatId);

    Either<IChatGame, ChatGameException> getChatGame(Long chatId);

    Either<IChatGame, ChatGameException> newGame(Long chatId, int complexity);

    Either<IChatGame, ChatGameException> makeTurn(Long chatId, String guessMsg);

    Either<IChatGame, ChatGameException> updateReplaceMessage(Long chatId);

    void setLastMessageId(Long chatId, Integer messageId);


}
