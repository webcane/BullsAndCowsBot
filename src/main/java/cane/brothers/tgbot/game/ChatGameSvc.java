package cane.brothers.tgbot.game;

import cane.brothers.game.GuessComplexityException;
import cane.brothers.game.GuessTurnException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
class ChatGameSvc implements ChatGameService {

    private final ChatGameRepository chatRepo;
    private final ChatGameSettingsService settingsSvc;
    private final @Lazy ConversionService conversionSvc;

    @Override
    @Transactional(readOnly = true)
    public boolean isInProgress(Long chatId) {
        try {
            var guessGame = getChatGame(chatId, false).getCurrentGame();
            return guessGame != null && !guessGame.isWin() && !guessGame.isFinished();
        } catch (ChatGameException ex) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isWin(Long chatId) {
        try {
            var guessGame = getChatGame(chatId, false).getCurrentGame();
            return guessGame != null && guessGame.isWin();
        } catch (ChatGameException ex) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public IChatGame getChatGame(Long chatId) throws ChatGameException {
        // get current chat game
        var chatGame = getChatGame(chatId, false);
        return conversionSvc.convert(chatGame, IChatGame.class);
    }

    @Override
    @Transactional
    public void finishGame(Long chatId) throws ChatGameException {
        // finish current guess game
        var chatGame = getChatGame(chatId, false);
        chatRepo.finishGame(chatGame);
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
            return conversionSvc.convert(chatGame, IChatGame.class);

        } catch (GuessComplexityException e) {
            throw new ChatGameException(chatId, e.getMessage(), e);
        }
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
            return conversionSvc.convert(chatGame, IChatGame.class);
        } catch (GuessComplexityException | GuessTurnException e) {
            log.error(e.getMessage());
            throw new ChatGameException(chatId, e.getMessage());
        }
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
    @Transactional(readOnly = true)
    public SortedSet<IGuessGame> getAllGames(Long chatId) throws ChatGameException {
        var chatGame = getChatGame(chatId, false);
        return chatGame.getAllGames().stream()
                .map(gg -> conversionSvc.convert(gg, IGuessGame.class))
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingInt(IGuessGame::getOrdinal))));
    }
}
