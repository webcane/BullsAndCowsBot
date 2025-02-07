package cane.brothers.tgbot.game;

import lombok.RequiredArgsConstructor;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatGameFormatterRegistrar implements FormatterRegistrar {

    private final ChatGameConverter chatGameConverter;
    private final GuessGameConverter guessGameConverter;
    private final GuessTurnConverter guessTurnConverter;
    private final ArrayToGuessNumberConverter arrayToGuessNumberConverter;

    @Override
    public void registerFormatters(FormatterRegistry registry) {
        registry.addConverter(chatGameConverter);
        registry.addConverter(guessGameConverter);
        registry.addConverter(guessTurnConverter);
        registry.addConverter(arrayToGuessNumberConverter);
    }
}
