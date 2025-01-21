package cane.brothers.tgbot.game;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.transaction.annotation.Transactional;

import java.sql.JDBCType;

/**
 * Implementation of custom logic for a chat game aggregate repository.
 */
@RequiredArgsConstructor
class ChatGameAggregateRepositoryImpl implements ChatGameAggregateRepository {

    private final NamedParameterJdbcOperations template;

    private final GuessNumberSQLType guessSqlType = new GuessNumberSQLType();


    @Transactional
    @Override
    public void startNewGame(ChatGame chat) {
        var currentGame = chat.getCurrentGame();
        MapSqlParameterSource insertParams = new MapSqlParameterSource();
        insertParams.addValue("id", chat.getId(), JDBCType.OTHER.getVendorTypeNumber());
        insertParams.addValue("ordinal", chat.getAllGames().size(), JDBCType.INTEGER.getVendorTypeNumber());
        insertParams.addValue("complexity", currentGame.getComplexity(), JDBCType.INTEGER.getVendorTypeNumber());
        insertParams.addValue("secret", currentGame.getSecret().getDigits(), guessSqlType.getVendorTypeNumber(), guessSqlType.getName());
        template.update("INSERT INTO guess_game (chat_game, ordinal, complexity, secret) VALUES (:id, :ordinal, :complexity, :secret)", insertParams);

        MapSqlParameterSource updateParams = new MapSqlParameterSource();
        updateParams.addValue("id", chat.getId(), JDBCType.OTHER.getVendorTypeNumber());
        updateParams.addValue("version", chat.getVersion(), JDBCType.INTEGER.getVendorTypeNumber());
        final int updateCount = template.update("UPDATE chat_game SET version = :version + 1 WHERE id = :id AND version = :version", updateParams);
        if (updateCount != 1) {
            var msg = String.format("chat game %d was changed before a new guess game was given", chat.getChatId());
            throw new OptimisticLockingFailureException(msg);
        }
    }

    @Transactional
    @Override
    public void makeTurn(ChatGame chat) {
        var currentGame = chat.getCurrentGame();
        var currentTurn = currentGame.getCurrentTurn();
        MapSqlParameterSource insertParams = new MapSqlParameterSource();
        insertParams.addValue("bulls", currentTurn.getBulls(), JDBCType.INTEGER.getVendorTypeNumber());
        insertParams.addValue("cows", currentTurn.getCows(), JDBCType.INTEGER.getVendorTypeNumber());
        insertParams.addValue("guess", currentTurn.getGuess().getDigits(), guessSqlType.getVendorTypeNumber(), guessSqlType.getName());
        insertParams.addValue("game_id", currentGame.getGameId(), JDBCType.OTHER.getVendorTypeNumber());
        insertParams.addValue("move_time", currentTurn.getMoveTime(), JDBCType.TIMESTAMP_WITH_TIMEZONE.getVendorTypeNumber());
        insertParams.addValue("ordinal", currentGame.getTurns().size(), JDBCType.INTEGER.getVendorTypeNumber());
        template.update("INSERT INTO guess_turn (bulls, cows, guess, guess_game, move_time, ordinal) VALUES (:bulls, :cows, :guess, :game_id, :move_time, :ordinal)", insertParams);

        MapSqlParameterSource updateParams = new MapSqlParameterSource();
        updateParams.addValue("id", chat.getId(), JDBCType.OTHER.getVendorTypeNumber());
        updateParams.addValue("version", chat.getVersion(), JDBCType.INTEGER.getVendorTypeNumber());
        final int updateCount = template.update("UPDATE chat_game SET version = :version + 1 WHERE id = :id AND version = :version", updateParams);
        if (updateCount != 1) {
            var msg = String.format("chat game %d was changed before a new game turn was given", chat.getChatId());
            throw new OptimisticLockingFailureException(msg);
        }
    }

    @Transactional
    @Override
    public void updateMessageId(ChatGame chat) {
        MapSqlParameterSource updateParams = new MapSqlParameterSource();
        updateParams.addValue("id", chat.getId(), JDBCType.OTHER.getVendorTypeNumber());
        updateParams.addValue("version", chat.getVersion(), JDBCType.INTEGER.getVendorTypeNumber());
        updateParams.addValue("message_id", chat.getLastMessageId(), JDBCType.INTEGER.getVendorTypeNumber());
        final int updateCount = template.update("UPDATE chat_game SET version = :version + 1, last_message_id = :message_id WHERE id = :id AND version = :version", updateParams);
        if (updateCount != 1) {
            var msg = String.format("chat game %d was changed before a last message id was given", chat.getChatId());
            throw new OptimisticLockingFailureException(msg);
        }
    }
}
