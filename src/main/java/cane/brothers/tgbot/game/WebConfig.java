package cane.brothers.tgbot.game;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
class WebConfig implements WebMvcConfigurer {

    private final ChatGameFormatterRegistrar chatGameFormatterRegistrar;

    @Override
    public void addFormatters(@NotNull FormatterRegistry registry) {
        chatGameFormatterRegistrar.registerFormatters(registry);
    }
}
