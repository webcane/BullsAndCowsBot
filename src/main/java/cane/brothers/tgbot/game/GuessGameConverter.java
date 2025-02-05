package cane.brothers.tgbot.game;

import cane.brothers.game.IGuessTurn;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
@RequiredArgsConstructor
class GuessGameConverter implements Converter<GuessGame, IGuessGame> {

    private final ConversionService conversionSvc;

    @NotNull
    @Override
    public IGuessGame convert(@NotNull GuessGame source) {
        return new IGuessGame() {

            @Override
            public boolean isWin() {
                return source.isWin();
            }

            @Override
            public LinkedList<IGuessTurn> getTurns() {
                LinkedList<IGuessTurn> result = new LinkedList<>();
                for (GuessTurn turn : source.getTurns()) {
                    result.add(conversionSvc.convert(turn, IGuessTurn.class));
                }
                return result;
            }
        };
    }
}
