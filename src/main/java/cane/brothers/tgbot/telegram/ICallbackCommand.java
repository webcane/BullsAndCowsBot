package cane.brothers.tgbot.telegram;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;

import java.io.Serializable;
import java.util.LinkedList;

public interface ICallbackCommand {

    LinkedList<BotApiMethod<? extends Serializable>> replyCallback();
}
