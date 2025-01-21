package cane.brothers.tgbot.game;

import cane.brothers.game.IGuessNumber;

import java.util.Arrays;

record GuessNumber(int[] digits) implements IGuessNumber {

    @Override
    public int getComplexity() {
        return digits.length;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int[] getDigits() {
        return this.digits;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.digits);
    }
}
