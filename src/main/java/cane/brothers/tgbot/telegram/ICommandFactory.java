package cane.brothers.tgbot.telegram;

import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

interface ICommandFactory<T extends BotApiObject> {

    IChatCommand<T> getCommand();
}
