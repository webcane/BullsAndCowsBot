package cane.brothers.tgbot.game;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
class WebConfig implements WebMvcConfigurer {

    private final GuessGameConverter guessGameConverter;
    private final GuessTurnConverter guessTurnConverter;
    private final ChatGameConverter chatGameConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(chatGameConverter);
        registry.addConverter(guessGameConverter);
        registry.addConverter(guessTurnConverter);
    }
}
