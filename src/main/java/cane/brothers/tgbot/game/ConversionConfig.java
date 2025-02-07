package cane.brothers.tgbot.game;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;

@Configuration
public class ConversionConfig {

    @Bean
    ChatGameConverter chatGameConverter(@Lazy ConversionService conversionSvc) {
        return new ChatGameConverter(conversionSvc);
    }

    @Bean
    GuessGameConverter guessGameConverter(@Lazy ConversionService conversionSvc) {
        return new GuessGameConverter(conversionSvc);
    }

    @Bean
    GuessTurnConverter guessTurnConverter() {
        return new GuessTurnConverter();
    }
}
