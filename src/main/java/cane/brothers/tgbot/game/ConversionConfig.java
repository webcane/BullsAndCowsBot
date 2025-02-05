package cane.brothers.tgbot.game;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.ConversionService;

@Configuration
public class ConversionConfig {

    @Bean
    @Primary
    public ConversionService tgBotConversionService(@Qualifier("mvcConversionService") final ConversionService conversionService) {
        return conversionService;
    }

    @Bean
    public ChatGameConverter chatGameConverter(@Lazy ConversionService conversionSvc) {
        return new ChatGameConverter(conversionSvc);
    }

    @Bean
    public GuessGameConverter guessGameConverter(@Lazy ConversionService conversionSvc) {
        return new GuessGameConverter(conversionSvc);
    }

    @Bean
    public GuessTurnConverter guessTurnConverter() {
        return new GuessTurnConverter();
    }
}
