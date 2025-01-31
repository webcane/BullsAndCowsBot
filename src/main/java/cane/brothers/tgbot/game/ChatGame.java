package cane.brothers.tgbot.game;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Version;

import java.util.*;


@Data
class ChatGame {

    @Id
    private UUID id;
    @Version
    private int version;
    private Long chatId;
    private Integer lastMessageId;
    private SortedSet<GuessGame> allGames = new TreeSet<>(Comparator.comparingInt(GuessGame::getOrdinal));

    ChatGame(Long chatId) {
        this.chatId = chatId;
    }

    @PersistenceCreator
    private ChatGame(UUID id, Long chatId, Integer lastMessageId, Collection<GuessGame> allGames, int version) {
        this.id = id;
        this.chatId = chatId;
        this.lastMessageId = lastMessageId;
        this.allGames.addAll(allGames);
        this.version = version;
    }

    public GuessGame getCurrentGame() {
        return allGames.last();
    }

    public void addNewGame(GuessGame newGame) {
        var ordinal = getAllGames().size();
        newGame.setOrdinal(++ordinal);
        getAllGames().add(newGame);
    }

    public void addTurn(GuessTurn newTurn) {
        getCurrentGame().addTurn(newTurn);
    }
}
