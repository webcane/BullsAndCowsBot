package cane.brothers.tgbot.telegram;

import cane.brothers.GameNumber;
import cane.brothers.GuessNumber;
import cane.brothers.GuessResult;
import cane.brothers.tgbot.emoji.GameEmoji;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.HashMap;
import java.util.Map;

public class TgBotGame {

    private final Map<Long, GameNumber> chatGame = new HashMap<>();

    public GameNumber newGame(Long chatId) {
        return chatGame.computeIfAbsent(chatId, k -> new GameNumber());
    }

    GameNumber getSecret(Long chatId) {
        return chatGame.get(chatId);
    }

    String getEmojiResult(GuessResult result) {
        return String.valueOf(GameEmoji.getDigit(result.getBulls())) +
                GameEmoji.BULL +
                GameEmoji.getDigit(result.getCows()) +
                GameEmoji.COW;
    }

    public SendMessage getGuessMessage(Long chatId, String userMessage) {
        GuessNumber guess = new GuessNumber(userMessage, getSecret(chatId).getLength());
        String guessMessage = "Try again";
        if (guess.isValid()) {
            GameNumber secret = getSecret(chatId);
            GuessResult result = secret.match(guess);
            guessMessage = result.isWin() ? "Win!!!" : getEmojiResult(result);
        }
        return SendMessage.builder().text(guessMessage).chatId(chatId).build();
    }
}
