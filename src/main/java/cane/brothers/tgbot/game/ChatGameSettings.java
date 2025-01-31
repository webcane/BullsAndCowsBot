package cane.brothers.tgbot.game;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("chat_game_settings")
class ChatGameSettings {
    @Id
    private int id;
    // default complexity for all new games
    private int complexity;
    // replace previous result message all show all
    private boolean replaceMessage;
    private boolean showAllTurns;
    // reply debug message in case of errors
    private boolean debug;
    // one to one relation to the chat game
    private Long chatId;

    @PersistenceCreator
    ChatGameSettings(Long chatId, int complexity, boolean replaceMessage, boolean showAllTurns, boolean debug) {
        this.chatId = chatId;
        this.complexity = complexity;
        this.replaceMessage = replaceMessage;
        this.showAllTurns = showAllTurns;
        this.debug = debug;
    }
}
