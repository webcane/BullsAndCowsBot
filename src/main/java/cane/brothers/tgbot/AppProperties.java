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

    public record HttpProxy(String hostname, Integer port, String username, String password) {
    }
}
