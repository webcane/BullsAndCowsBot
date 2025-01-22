package cane.brothers.tgbot.game;

import cane.brothers.game.IGuessNumber;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;

import java.util.*;

@Data
class GuessGame {

    @Id
    private UUID gameId;
    private int complexity;
    private IGuessNumber secret;
    private int ordinal;
    private SortedSet<GuessTurn> turns = new TreeSet<>(Comparator.comparingInt(GuessTurn::getOrdinal));

    /**
     * Save constructor
     *
     * @param secret the secret number
     */
    public GuessGame(IGuessNumber secret) {
        this.secret = secret;
        this.complexity = secret.getComplexity();
    }

    /**
     * Load constructor
     *
     * @param gameId     db entity id
     * @param complexity complexity
     * @param secret     secret
     * @param ordinal    ordinal of a game
     * @param turns      collection of turns
     */
    @PersistenceCreator
    private GuessGame(UUID gameId, int complexity, IGuessNumber secret, int ordinal, Collection<GuessTurn> turns) {
        this.gameId = gameId;
        this.complexity = complexity;
        this.secret = secret;
        this.ordinal = ordinal;
        this.turns.addAll(turns);
    }

    public GuessTurn getCurrentTurn() {
        return turns.last();
    }

    public void addTurn(GuessTurn newTurn) {
        var ordinal = getTurns().size();
        newTurn.setOrdinal(++ordinal);
        getTurns().add(newTurn);
    }

    public boolean isWin() {
        return !turns.isEmpty() && getCurrentTurn().isWin();
    }
}
