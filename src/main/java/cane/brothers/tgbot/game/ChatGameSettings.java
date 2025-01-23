package cane.brothers.tgbot.game;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("chat_game_settings")
class ChatGameSettings {
    @Id
    private int id;
    // default complexity for all new games
    private int complexity;
    // replace previous result message all show all
    private boolean replaceMessage = true;
    // reply debug message in case of errors
    private boolean debug = false;
    // one to one relation to the chat game
    private AggregateReference<ChatGame, UUID> chatGame;

    ChatGameSettings(int complexity, AggregateReference<ChatGame, UUID> chatGame) {
        this.complexity = complexity;
        this.chatGame = chatGame;
    }
}
