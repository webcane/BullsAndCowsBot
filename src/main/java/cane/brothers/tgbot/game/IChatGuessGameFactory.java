package cane.brothers.tgbot.game;

import cane.brothers.game.GuessTurnException;
import cane.brothers.game.IStoredGuessGameFactory;

interface IChatGuessGameFactory extends IStoredGuessGameFactory {

    GuessGame newGuessGame();

    GuessTurn newTurn(String guess) throws GuessTurnException;

}
