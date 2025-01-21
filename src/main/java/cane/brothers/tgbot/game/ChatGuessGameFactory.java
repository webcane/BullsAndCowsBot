package cane.brothers.tgbot.game;

import cane.brothers.game.GuessComplexityException;
import cane.brothers.game.GuessTurnException;
import cane.brothers.game.StoredGuessGameNumberFactory;

class ChatGuessGameFactory extends StoredGuessGameNumberFactory implements IChatGuessGameFactory {

    public ChatGuessGameFactory(int complexity) throws GuessComplexityException {
        super(complexity);
    }

    public ChatGuessGameFactory(GuessGame guessGame) throws GuessComplexityException {
        super(guessGame.getSecret());
    }

    @Override
    public GuessGame newGuessGame() {
        return new GuessGame(this.secret);
    }

    @Override
    public GuessTurn newTurn(String number) throws GuessTurnException {
        var guessTurn = makeTurn(number);
        return new GuessTurn(this.guess, guessTurn.getBulls(), guessTurn.getCows());
    }
}
