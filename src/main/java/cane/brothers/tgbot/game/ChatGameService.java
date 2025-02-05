package cane.brothers.tgbot.game;

public interface ChatGameService {

    boolean isInProgress(Long chatId);

    boolean isWin(Long chatId);

    IChatGame getChatGame(Long chatId) throws ChatGameException;

    void finishGame(Long chatId) throws ChatGameException;

    IChatGame newGame(Long chatId) throws ChatGameException;

    IChatGame makeTurn(Long chatId, String guessMsg) throws ChatGameException;

    void setLastMessageId(Long chatId, Integer messageId) throws ChatGameException;
}
