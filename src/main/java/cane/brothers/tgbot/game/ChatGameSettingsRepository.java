package cane.brothers.tgbot.game;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface ChatGameSettingsRepository extends CrudRepository<ChatGameSettings, Integer> {

    Optional<ChatGameSettings> findByChatId(@Param("chat_id") Long chatId);
}
