package cane.brothers.tgbot.game;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface ChatGameSettingsRepository extends CrudRepository<ChatGameSettings, Integer> {

    // select chat_game_settings from chat_game_settings left join chat_game ON chat_game_settings.chat = chat_game.id where chat_game.chat_id = 396702390
    Optional<ChatGameSettings> findByChatGame(@Param("chat_game") ChatGame chatGame);

    // TODO
//    @Transactional
//    @Override
//    public void updateReplaceMessage(ChatGame chat) {
//        MapSqlParameterSource updateParams = new MapSqlParameterSource();
//        updateParams.addValue("id", chat.getId(), JDBCType.OTHER.getVendorTypeNumber());
//        updateParams.addValue("version", chat.getVersion(), JDBCType.INTEGER.getVendorTypeNumber());
//        updateParams.addValue("replace_message", chat.isReplaceMessage(), JDBCType.BOOLEAN.getVendorTypeNumber());
//        final int updateCount = template.update("UPDATE chat_game SET version = :version + 1, replace_message = :replace_message WHERE id = :id AND version = :version", updateParams);
//        if (updateCount != 1) {
//            var msg = String.format("chat game %d was changed before a replace_message was given", chat.getChatId());
//            throw new OptimisticLockingFailureException(msg);
//        }
//    }

}
