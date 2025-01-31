package cane.brothers.tgbot.game;

import cane.brothers.game.GuessComplexityException;
import cane.brothers.game.GuessTurnException;
import cane.brothers.game.IGuessTurn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
class ChatGameSvc implements ChatGameService {

    private final ChatGameRepository chatRepo;
    private final ChatGameSettingsService settingsSvc;

    @Override
    @Transactional
    public IChatGame getChatGame(Long chatId) throws ChatGameException {
        // get current chat game
        var chatGame = getChatGame(chatId, false);
        return convertChatGame(chatGame);
    }

    @Override
    public void finishGame(Long chatId) throws ChatGameException {
        // finish current guess game
        var chatGame = getChatGame(chatId, false);
        chatRepo.finishGame(chatGame);
    }

    @Override
    public boolean isWin(Long chatId) throws ChatGameException {
        var guessGame = getChatGame(chatId, false).getCurrentGame();
        return guessGame != null &&guessGame.isWin();
    }

    ChatGame getChatGame(Long chatId, boolean createIfAbsence) throws ChatGameException {
        Optional<ChatGame> chatGame = chatRepo.findByChatId(chatId);
        if (createIfAbsence && chatGame.isEmpty()) {
            return chatGame.orElse(chatRepo.save(new ChatGame(chatId)));
        }
        return chatGame.orElseThrow(
                () -> new ChatGameException(chatId, "There is no active game"));
    }

    @Override
    @Transactional
    public IChatGame newGame(Long chatId) throws ChatGameException {
        try {
            // get chat game or create new one
            ChatGame chatGame = getChatGame(chatId, true);

            ChatGuessGameFactory chatFactory = new ChatGuessGameFactory(settingsSvc.getComplexity(chatId));
            var guessGame = chatFactory.newGuessGame();

            chatGame.addNewGame(guessGame);
            chatRepo.startNewGame(chatGame);

            chatGame = getChatGame(chatId, false);
            return convertChatGame(chatGame);

        } catch (GuessComplexityException e) {
            throw new ChatGameException(chatId, e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInProgress(Long chatId) throws ChatGameException {
        var guessGame = getChatGame(chatId, false).getCurrentGame();
        return guessGame != null && !guessGame.isWin() && !guessGame.isFinished();
    }

    @Override
    @Transactional
    public IChatGame makeTurn(Long chatId, String guessMsg) throws ChatGameException {
        try {
            ChatGame chatGame = getChatGame(chatId, false);
            var guessGame = Optional.ofNullable(chatGame.getCurrentGame()).orElseThrow(
                    () -> new ChatGameException(chatId, "There is no active guess game. please start new one"));

            var chatFactory = new ChatGuessGameFactory(guessGame);
            var currentTurn = chatFactory.newTurn(guessMsg);

            chatGame.addTurn(currentTurn);
            chatRepo.makeTurn(chatGame);

            chatGame = getChatGame(chatId, false);
            return convertChatGame(chatGame);
        } catch (GuessComplexityException | GuessTurnException e) {
            log.error(e.getMessage());
            throw new ChatGameException(chatId, e.getMessage());
        }
    }

    // TODO extract converters
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

            @Override
            public IGuessGame getCurrentGame() {
                return convertGuessGame(source.getCurrentGame());
            }
        };
    }

    private IGuessGame convertGuessGame(GuessGame source) {
        return new IGuessGame() {

            @Override
            public boolean isWin() {
                return source.isWin();
            }

            @Override
            public LinkedList<IGuessTurn> getTurns() {
                LinkedList<IGuessTurn> result = new LinkedList<>();
                for (GuessTurn turn : source.getTurns()) {
                    result.add(convertTurn(turn));
                }
                return result;
            }
        };
    }

    private IGuessTurn convertTurn(GuessTurn source) {
        return new IGuessTurn() {

            @Override
            public boolean isWin() {
                return source.isWin();
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
    @Transactional
    public void setLastMessageId(Long chatId, Integer messageId) throws ChatGameException {
        // update message id
        var chatGame = getChatGame(chatId, false);
        chatGame.setLastMessageId(messageId);
        chatRepo.updateMessageId(chatGame);
    }

    @Override
    public Integer getLastMessageId(Long chatId) throws ChatGameException {
        var chatGame = getChatGame(chatId, false);
        return chatGame.getLastMessageId();
    }
}
