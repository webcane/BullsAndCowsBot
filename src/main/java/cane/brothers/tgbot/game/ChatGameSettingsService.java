package cane.brothers.tgbot.game;

public interface ChatGameSettingsService {

    boolean isReplaceMessage(Long chatId);

    boolean isDebug(Long chatId);

    void updateReplaceMessage(Long chatId);

    int getComplexity(Long chatId);

    void setComplexity(Long chatId, int complexity);
}
