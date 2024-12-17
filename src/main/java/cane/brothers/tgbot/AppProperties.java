package cane.brothers.tgbot;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "tgbot")
public record AppProperties(
        @NotNull(message = "The property 'tgbot.token' is required")
        String token,
        HttpProxy proxy) {

    public record HttpProxy(
            @NotNull(message = "The property 'tgbot.proxy.hostname' is required")
            String hostname,
            @NotNull(message = "The property 'tgbot.proxy.port' is required")
            int port, String username, String password) {
    }
}
