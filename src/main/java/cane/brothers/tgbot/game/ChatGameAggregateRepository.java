package cane.brothers.tgbot.game;

interface ChatGameAggregateRepository {

    void startNewGame(ChatGame chat);

    void makeTurn(ChatGame chat);

    void updateMessageId(ChatGame chat);
}
