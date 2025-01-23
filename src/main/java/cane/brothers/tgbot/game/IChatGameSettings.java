package cane.brothers.tgbot.game;

public interface IChatGameSettings {

    int getComplexity();
    // replace previous result message all show all
    boolean isReplaceMessage();
    // reply debug message in case of errors
    boolean isDebug();
}
