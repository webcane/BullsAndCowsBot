package cane.brothers.tgbot.game;

import lombok.Getter;

@Getter
public class ChatGameException extends Exception {

    private final Long chatId;

    public ChatGameException(Long chatId, String message) {
        super(message);
        this.chatId = chatId;
    }

    public ChatGameException(Long chatId, String message, Throwable cause) {
        super(message, cause);
        this.chatId = chatId;
    }
}
