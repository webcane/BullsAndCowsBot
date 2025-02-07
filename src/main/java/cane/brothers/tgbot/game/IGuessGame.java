package cane.brothers.tgbot.game;

import cane.brothers.game.IGuessTurn;

import java.util.LinkedList;

public interface IGuessGame {

    boolean isWin();

    int getOrdinal();

    LinkedList<IGuessTurn> getTurns();
}
