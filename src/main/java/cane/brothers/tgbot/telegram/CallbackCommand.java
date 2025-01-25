package cane.brothers.tgbot.telegram;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public enum CallbackCommand implements ICallbackCommand {
    // TODO         // replace_message - замещать последнее сообщение
    COMPLEXITY {
        @Override
        public LinkedList<BotApiMethod<? extends Serializable>> replyCallback() {
            LinkedList<BotApiMethod<? extends Serializable>> list = new LinkedList<>();
            EditMessageText newTxt = EditMessageText.builder()
                 //   .parseMode("HTML")
                    .chatId(chatId)
                    .messageId(messageId)
                    .text("Choose game complexity:")
                    .build();
            list.add(newTxt);

            EditMessageReplyMarkup newKB = EditMessageReplyMarkup.builder()
                    .chatId(chatId).messageId(messageId).replyMarkup(getKeyboardMarkup()).build();
            list.add(newKB);
            return list;
        }

        InlineKeyboardMarkup getKeyboardMarkup() {
            List<InlineKeyboardButton> inlineButtons = new ArrayList<>();
            for (String c : List.of("1", "2", "3", "4", "5", "6")) {
                inlineButtons.add(InlineKeyboardButton.builder().text(c).callbackData("complexity=" + c).build());
            }
            return InlineKeyboardMarkup.builder()
                    .keyboardRow(new InlineKeyboardRow(inlineButtons))
                    .keyboardRow(new InlineKeyboardRow(InlineKeyboardButton.builder().text("<< Back to Settings")
                            .callbackData("settings").build()))
                    .build();
        }
    },
    SETTINGS {
        @Override
        public LinkedList<BotApiMethod<? extends Serializable>> replyCallback() {
            LinkedList<BotApiMethod<? extends Serializable>> list = new LinkedList<>();
            EditMessageText newTxt = EditMessageText.builder()
                    .parseMode("HTML")
                    .chatId(chatId)
                    .messageId(messageId)
                    .text("Bulls & Cows settings:")
                    .build();
            list.add(newTxt);

            EditMessageReplyMarkup newKB = EditMessageReplyMarkup.builder()
                    .chatId(chatId).messageId(messageId).replyMarkup(getKeyboardMarkup()).build();
            list.add(newKB);
            return list;
        }

        InlineKeyboardMarkup getKeyboardMarkup() {
            var complexityButton = InlineKeyboardButton.builder().text("Complexity").callbackData("complexity").build();
            var hideButton = InlineKeyboardButton.builder().text("Hide").callbackData("hide_settings").build();
            return InlineKeyboardMarkup.builder()
                    .keyboardRow(new InlineKeyboardRow(List.of(complexityButton)))
                    .keyboardRow(new InlineKeyboardRow(hideButton))
                    .build();
        }
    },
    HIDE_SETTINGS {
        @Override
        public LinkedList<BotApiMethod<? extends Serializable>> replyCallback() {
            LinkedList<BotApiMethod<? extends Serializable>> list = new LinkedList<>();
            list.add(DeleteMessage.builder().chatId(chatId).messageId(messageId).build());
            return list;
        }
    },
    UNKNOWN;

    Long chatId;
    Integer messageId;

    public static CallbackCommand fromString(String msg, Long chatId, Integer messageId) {
        var cmd = Arrays.stream(CallbackCommand.values())
                .filter(command -> command.name().equals(msg.toUpperCase()))
                .findFirst().orElse(CallbackCommand.UNKNOWN);
        cmd.setChatId(chatId);
        cmd.setMessageId(messageId);
        return cmd;
    }

    @Override
    public LinkedList<BotApiMethod<? extends Serializable>> replyCallback() {
        // do not reply to callback
        return new LinkedList<>();
    }


    void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }
}
