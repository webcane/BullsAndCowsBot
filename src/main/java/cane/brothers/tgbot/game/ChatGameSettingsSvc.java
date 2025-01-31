package cane.brothers.tgbot.game;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatGameSettingsSvc implements ChatGameSettingsService {

    private static final int DEFAULT_GAME_COMPLEXITY = 4;
    // replace previous result message or show new one
    private static final boolean DEFAULT_REPLACE_ANSWER_MESSAGE = true;
    // show all turns in result message or only last turn
    private static final boolean DEFAULT_SHOW_ALL_TURNS = true;
    // reply debug message in case of errors
    private static final boolean DEFAULT_DEBUG_MESSAGE = false;

    private final ChatGameSettingsRepository settingsRepo;

    /**
     * Create settings if missing
     *
     * @param chatId chat_id
     * @return ChatGameSettings
     */
    ChatGameSettings getGameSettings(Long chatId) {
        ChatGameSettings gameSettings;
        var opt = settingsRepo.findByChatId(chatId);
        if (opt.isPresent()) {
            gameSettings = opt.get();
        } else {
            gameSettings = new ChatGameSettings(chatId,
                    DEFAULT_GAME_COMPLEXITY,
                    DEFAULT_REPLACE_ANSWER_MESSAGE,
                    DEFAULT_SHOW_ALL_TURNS,
                    DEFAULT_DEBUG_MESSAGE);
            gameSettings = settingsRepo.save(gameSettings);
        }
        return gameSettings;
    }

    @Override
    @Transactional
    public boolean isReplaceMessage(Long chatId) {
        return getGameSettings(chatId).isReplaceMessage();
    }

    @Override
    @Transactional
    public void updateReplaceMessage(Long chatId) {
        var gameSettings = getGameSettings(chatId);
        var replaceMessage = !gameSettings.isReplaceMessage();
        log.info("chat {}. {} replace_message", chatId, (replaceMessage ? "enable" : "disable"));
        gameSettings.setReplaceMessage(replaceMessage);
        settingsRepo.save(gameSettings);
    }

    @Override
    @Transactional
    public int getComplexity(Long chatId) {
        return getGameSettings(chatId).getComplexity();
    }

    @Override
    @Transactional
    public void setComplexity(Long chatId, int complexity) {
        var gameSettings = getGameSettings(chatId);
        log.info("chat {}. complexity {}", chatId, complexity);
        gameSettings.setComplexity(complexity);
        settingsRepo.save(gameSettings);
    }

    @Override
    @Transactional
    public boolean isDebug(Long chatId) {
        return getGameSettings(chatId).isDebug();
    }

    @Override
    @Transactional
    public boolean isShowAllTurns(Long chatId) {
        return getGameSettings(chatId).isShowAllTurns();
    }

    @Override
    @Transactional
    public void updateShowAllTurns(Long chatId) {
        var gameSettings = getGameSettings(chatId);
        var showAllTurns = !gameSettings.isShowAllTurns();
        log.info("chat {}. {} show_all_turns", chatId, (showAllTurns ? "enable" : "disable"));
        gameSettings.setShowAllTurns(showAllTurns);
        settingsRepo.save(gameSettings);
    }
}
