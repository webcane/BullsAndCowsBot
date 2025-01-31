package cane.brothers.tgbot.game;

public interface ChatGameSettingsService {

    boolean isReplaceMessage(Long chatId);

    void updateReplaceMessage(Long chatId);

    int getComplexity(Long chatId);

    void setComplexity(Long chatId, int complexity);

    boolean isDebug(Long chatId);

    boolean isShowAllTurns(Long chatId);

    void updateShowAllTurns(Long chatId);
}
