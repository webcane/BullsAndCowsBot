package cane.brothers.tgbot.game;

import cane.brothers.game.IGuessNumber;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
class GuessTurn {

    private IGuessNumber guess;
    private int bulls;
    private int cows;
    private OffsetDateTime moveTime;
    private int ordinal;

    public GuessTurn(IGuessNumber guess, int bulls, int cows) {
        this.guess = guess;
        this.bulls = bulls;
        this.cows = cows;
    }

    public boolean isWin() {
        return bulls == guess.getComplexity();
    }
}
