package cane.brothers.tgbot.telegram;

import cane.brothers.GameNumber;

import java.util.HashMap;
import java.util.Map;

public class TgBotGame {

    private final Map<Long, GameNumber> chatGame = new HashMap<>();

    public GameNumber newGame(Long chatId) {
        return chatGame.put(chatId, new GameNumber());
    }

    public GameNumber getGame(Long chatId) {
        return chatGame.get(chatId);
    }
}
