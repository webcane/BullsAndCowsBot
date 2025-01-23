package cane.brothers.tgbot.game;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatGameSettingsSvc implements ChatGameSettingsService {

    private final ChatGameSettingsRepository settingsRepo;
    private final ChatGameRepository chatRepo;

    @Override
    public boolean isReplaceMessage(Long chatId) {
        try {
        ChatGame chatGame = chatRepo.findByChatId(chatId).orElseThrow();
        var gameSettings = settingsRepo.findByChatGame(chatGame).orElseThrow();
        return gameSettings.isReplaceMessage();
        } catch (NoSuchElementException ex) {
            return true;
        }
    }

    @Override
    public void updateReplaceMessage(Long chatId) {
        // var chatGame = getChatGame(chatId, false);
        // var replaceMessage = !chatGame.isReplaceMessage();
        // log.info(replaceMessage ? "enable replace_message" : "disable message_replace. keep all");
        // chatGame.setReplaceMessage(replaceMessage);
        // chatRepo.updateReplaceMessage(chatGame);

        // chatGame = getChatGame(chatId, false);
        // return Either.left(convertChatGame(chatGame));

        // TODO simplify
        // var gameSettings = settingsRepo.findByChatId(chatId).orElseThrow();
        ChatGame chatGame = chatRepo.findByChatId(chatId).orElseThrow();
        var gameSettings = settingsRepo.findByChatGame(chatGame).orElseThrow();

        var replaceMessage = !gameSettings.isReplaceMessage();
        log.info(replaceMessage ? "enable replace_message" : "disable message_replace. keep all");
        gameSettings.setReplaceMessage(replaceMessage);
        settingsRepo.save(gameSettings);
    }

    @Override
    public int getComplexity(Long chatId) {
        try {
            //var gameSettings = settingsRepo.findByChatId(chatId).orElseThrow();
            ChatGame chatGame = chatRepo.findByChatId(chatId).orElseThrow();
            var gameSettings = settingsRepo.findByChatGame(chatGame).orElseThrow();
            return gameSettings.getComplexity();
        } catch (NoSuchElementException ex) {
            return 4;
        }
    }

    @Override
    public void setComplexity(Long chatId, int complexity) {
        //var gameSettings = settingsRepo.findByChatId(chatId).orElseThrow();
        ChatGame chatGame = chatRepo.findByChatId(chatId).orElseThrow();
        var gameSettings = settingsRepo.findByChatGame(chatGame).orElseThrow();
        log.info("chat {} default complexity is {}", chatId, complexity);
        gameSettings.setComplexity(complexity);
        settingsRepo.save(gameSettings);
    }

    @Override
    public boolean isDebug(Long chatId) {
        try {
        //var gameSettings = settingsRepo.findByChatId(chatId).orElseThrow();
        ChatGame chatGame = chatRepo.findByChatId(chatId).orElseThrow();
        var gameSettings = settingsRepo.findByChatGame(chatGame).orElseThrow();
        return gameSettings.isDebug();
        } catch (NoSuchElementException ex) {
            return false;
        }
    }
}
