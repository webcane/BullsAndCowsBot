package cane.brothers.tgbot.game;

import cane.brothers.game.GuessComplexityException;
import cane.brothers.game.GuessTurnException;
import cane.brothers.game.IGuessTurn;
import io.jbock.util.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
class ChatGameSvc implements ChatGameService {

    private final ChatGameRepository chatRepo;

    @Override
    public Either<IChatGame, ChatGameException> getChatGame(Long chatId) {
        try {
            // get current chat game
            var chatGame = getChatGame(chatId, false);
            return Either.left(convertChatGame(chatGame));
        } catch (ChatGameException e) {
            log.error(e.getMessage());
            return Either.right(e);
        }
    }

    ChatGame getChatGame(Long chatId, boolean createIfAbsence) throws ChatGameException {
        Optional<ChatGame> chatGame = chatRepo.findByChatId(chatId);
        if (createIfAbsence && chatGame.isEmpty()) {
            return chatGame.orElse(chatRepo.save(new ChatGame(chatId)));
        }
        return chatGame.orElseThrow(
                () -> new ChatGameException(chatId, "There is no active game. Please, use the `/new` command"));
    }

    @Override
    public Either<IChatGame, ChatGameException> newGame(Long chatId, int complexity) {
        try {
            // get chat game or create new one
            ChatGame chatGame = getChatGame(chatId, true);

            ChatGuessGameFactory chatFactory = new ChatGuessGameFactory(complexity);
            var guessGame = chatFactory.newGuessGame();

            chatGame.addNewGame(guessGame);
            chatRepo.startNewGame(chatGame);

            chatGame = getChatGame(chatId, false);
            return Either.left(convertChatGame(chatGame));
        } catch (GuessComplexityException e) {
            log.error(e.getMessage());
            return Either.right(new ChatGameException(chatId, e.getMessage(), e));
        } catch (ChatGameException e) {
            log.error(e.getMessage());
            return Either.right(e);
        }
    }

    @Override
    public boolean isGameStarted(Long chatId) {
        try {
            return getChatGame(chatId, false).getCurrentGame() != null;
        } catch (ChatGameException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Either<IGuessTurn, ChatGameException> makeTurn(Long chatId, String guessMsg) {
        try {
            ChatGame chatGame = getChatGame(chatId, false);
            var guessGame = Optional.ofNullable(chatGame.getCurrentGame()).orElseThrow(
                    () -> new ChatGameException(chatId, "There is no active guess game. please start new one"));

            var chatFactory = new ChatGuessGameFactory(guessGame);
            var currentTurn = chatFactory.newTurn(guessMsg);

            chatGame.addTurn(currentTurn);
            chatRepo.makeTurn(chatGame);

            return Either.left(convertTurn(currentTurn));
        } catch (GuessComplexityException | GuessTurnException e) {
            log.error(e.getMessage());
            return Either.right(new ChatGameException(chatId, e.getMessage()));
        } catch (ChatGameException e) {
            log.error(e.getMessage());
            return Either.right(e);
        }
    }

    private IChatGame convertChatGame(ChatGame source) {
        return new IChatGame() {
            @Override
            public Long getChatId() {
                return source.getChatId();
            }

            @Override
            public Integer getLastMessageId() {
                return source.getLastMessageId();
            }

            public int getComplexity() {
                return source.getCurrentGame() == null ? -1 : source.getCurrentGame().getComplexity();
            }
        };
    }

    private IGuessTurn convertTurn(GuessTurn source) {
        return new IGuessTurn() {

            @Override
            public boolean isWin() {
                return getBulls() == getComplexity();
            }

            @Override
            public int[] getDigits() {
                return source.getGuess().getDigits();
            }

            @Override
            public boolean isValid() {
                return getBulls() + getCows() <= getComplexity();
            }

            @Override
            public int getComplexity() {
                return source.getGuess().getComplexity();
            }

            @Override
            public int getBulls() {
                return source.getBulls();
            }

            @Override
            public int getCows() {
                return source.getCows();
            }
        };
    }

    @Override
    public void setLastMessageId(Long chatId, Integer messageId) {
        try {
            // update message id
            var chatGame = getChatGame(chatId, false);
            chatGame.setLastMessageId(messageId);
            chatRepo.updateMessageId(chatGame);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }
}
