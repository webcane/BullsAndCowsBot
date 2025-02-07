package cane.brothers.tgbot.game;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface ChatGameRepository extends CrudRepository<ChatGame, UUID>, ChatGameAggregateRepository {

    Optional<ChatGame> findByChatId(@Param("chat_id") Long chatId);
}
