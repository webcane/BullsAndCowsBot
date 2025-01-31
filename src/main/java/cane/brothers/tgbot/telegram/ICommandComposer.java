package cane.brothers.tgbot.telegram;

import java.util.Deque;

public interface ICommandComposer {

    Deque<IChatCommand> getCommands();

    default boolean isUnknown() {
        return false;
    }
}
