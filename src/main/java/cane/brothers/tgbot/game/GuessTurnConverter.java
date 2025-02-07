package cane.brothers.tgbot.game;

import cane.brothers.game.IGuessTurn;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;

class GuessTurnConverter implements Converter<GuessTurn, IGuessTurn> {
    @NotNull
    @Override
    public IGuessTurn convert(@NotNull GuessTurn source) {
        return new IGuessTurn() {

            @Override
            public boolean isWin() {
                return source.isWin();
            }

            @Override
            public int[] getDigits() {
                return source.getGuess().getDigits();
            }

            @Override
            public boolean isValid() {
                return getBulls() + getCows() <= getComplexity();
            }

            @Override
            public int getComplexity() {
                return source.getGuess().getComplexity();
            }

            @Override
            public int getBulls() {
                return source.getBulls();
            }

            @Override
            public int getCows() {
                return source.getCows();
            }
        };
    }
}
