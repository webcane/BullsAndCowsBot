package cane.brothers.tgbot.telegram;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
enum ReplyCommand implements IReplyCommand {
    INFO {
        @Override
        public void execute(Message message, TelegramClient telegramClient) throws TelegramApiException {
            log.info("show info");
        }
    },
    SETTINGS {
        // replace_message - замещать последнее сообщение
        @Override
        public void execute(Message message, TelegramClient telegramClient) throws TelegramApiException {
            var chatId = message.getChatId();
            var reply = SendMessage.builder().text("Bulls & Cows settings:").chatId(chatId)
                    .replyMarkup(getSettingsKeyboardMarkup())
                    .build();
            telegramClient.execute(reply);
        }

    },
    // callback
    CALLBACK_ANSWER {
        @Override
        public void execute(CallbackQuery callbackQuery, TelegramClient telegramClient) throws TelegramApiException {
            var reply = AnswerCallbackQuery.builder().callbackQueryId(callbackQuery.getId())
                    .cacheTime(3600) // 1h
                    .build();
            telegramClient.execute(reply);
        }
    },
    CALLBACK_COMPLEXITY_TEXT {
        @Override
        public void execute(CallbackQuery callbackQuery, TelegramClient telegramClient) throws TelegramApiException {
            var reply = EditMessageText.builder().chatId(callbackQuery.getMessage().getChatId())
                    .parseMode("HTML")
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .text("Choose game <i>complexity</i>:")
                    .build();
            telegramClient.execute(reply);
        }
    },
    CALLBACK_COMPLEXITY_REPLY_MARKUP {
        @Override
        public void execute(CallbackQuery callbackQuery, TelegramClient telegramClient) throws TelegramApiException {
            var reply = EditMessageReplyMarkup.builder().chatId(callbackQuery.getMessage().getChatId())
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .replyMarkup(getKeyboardMarkup()).build();
            telegramClient.execute(reply);
        }

        InlineKeyboardMarkup getKeyboardMarkup() {
            List<InlineKeyboardButton> inlineButtons = new ArrayList<>();
            for (String c : List.of("1", "2", "3", "4", "5", "6")) {
                var data = String.format("%s=%s", ChatCommandComposer.COMPLEXITY, c);
                inlineButtons.add(InlineKeyboardButton.builder().text(c).callbackData(data).build());
            }
            return InlineKeyboardMarkup.builder()
                    .keyboardRow(new InlineKeyboardRow(inlineButtons))
                    .keyboardRow(new InlineKeyboardRow(InlineKeyboardButton.builder().text("<< Back to Settings")
                            .callbackData(ChatCommandComposer.MENU_SETTINGS.toString()).build()))
                    .build();
        }
    },
    CALLBACK_SETTINGS_REPLY_MARKUP {
        @Override
        public void execute(CallbackQuery callbackQuery, TelegramClient telegramClient) throws TelegramApiException {
            var reply = EditMessageReplyMarkup.builder().chatId(callbackQuery.getMessage().getChatId())
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .replyMarkup(getSettingsKeyboardMarkup()).build();
            telegramClient.execute(reply);
        }
    },
    CALLBACK_SETTINGS_TEXT {
        @Override
        public void execute(CallbackQuery callbackQuery, TelegramClient telegramClient) throws TelegramApiException {
            var reply = EditMessageText.builder().chatId(callbackQuery.getMessage().getChatId())
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .text("Bulls & Cows settings:")
                    .build();
            telegramClient.execute(reply);
        }
    },
    CALLBACK_HIDE_SETTINGS {
        @Override
        public void execute(CallbackQuery callbackQuery, TelegramClient telegramClient) throws TelegramApiException {
            var reply = DeleteMessage.builder().chatId(callbackQuery.getMessage().getChatId())
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .build();
            telegramClient.execute(reply);
        }
    };
//    HIDE_SETTINGS_REPLY_MARKUP {
//        @Override
//        public void execute(CallbackQuery callbackQuery, TelegramClient telegramClient) throws TelegramApiException {
//            var reply = EditMessageReplyMarkup.builder().chatId(callbackQuery.getMessage().getChatId())
//                    .messageId(callbackQuery.getMessage().getMessageId())
//                    .replyMarkup(new ReplyKeyboardRemove(true)).build();
//            telegramClient.execute(reply);
//        }
//    },

    //    HIDE_SETTINGS {
    //        // replace_message - замещать последнее сообщение
    //        @Override
    //        public BotApiMethod<? extends Serializable> getReply(Supplier<Either<IChatGame, ChatGameException>> commandSupplier) {
    //            AtomicReference<BotApiMethod.BotApiMethodBuilder<?, ?, ?>> reply = new AtomicReference<>();
    //
    //            commandSupplier.get().ifLeftOrElse(
    //                    chatGame -> reply.set(SendMessage.builder().chatId(chatGame.getChatId())
    //                            .replyMarkup(new ReplyKeyboardRemove(true))),
    //                    r -> reply.set(SendMessage.builder().chatId(r.getChatId())
    //                            .text(r.getMessage())));
    //            return reply.get().build();
    //        }
    //    },

//    REPLACE_MESSAGE {
//        @Override
//        public void execute(CallbackQuery callbackQuery, TelegramClient telegramClient) throws TelegramApiException {
//            // TODO replace_message - замещать последнее сообщение
//            // do not reply to callback
//        }
//    },

//    SHOW_TURNS {
//        @Override
//        public void execute(CallbackQuery callbackQuery, TelegramClient telegramClient) throws TelegramApiException {
//            // do not reply to callback
//        }
//    },

//    UNKNOWN {
//        @Override
//        public void execute(Message message, TelegramClient telegramClient) throws TelegramApiException {
//            var reply = SendMessage.builder().chatId(message.getChatId())
//                    .text("Unknown game settings command")
//                    .build();
//            telegramClient.execute(reply);
//        }
//
//        @Override
//        public void execute(CallbackQuery callbackQuery, TelegramClient telegramClient) throws TelegramApiException {
//            // do not reply to callback
//            var reply = SendMessage.builder().chatId(callbackQuery.getMessage().getChatId())
//                    .text("Unknown game settings callback")
//                    .build();
//            telegramClient.execute(reply);
//        }
//    };

    @Override
    public String toString() {
        return String.format("/%s", name().toLowerCase());
    }
}
