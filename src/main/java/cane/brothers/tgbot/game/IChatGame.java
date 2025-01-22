package cane.brothers.tgbot.game;

public interface IChatGame {

    Long getChatId();

    Integer getLastMessageId();

    int getComplexity();

    IGuessGame getCurrentGame();
}
