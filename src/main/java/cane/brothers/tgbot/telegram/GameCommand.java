package cane.brothers.tgbot.telegram;

import cane.brothers.tgbot.game.ChatGameException;
import cane.brothers.tgbot.game.IChatGame;
import io.jbock.util.Either;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Slf4j
enum GameCommand implements IGameCommand {
    NEW {
        @Override
        public BotApiMethod.BotApiMethodBuilder<?, ?, ?> getReply(Supplier<Either<IChatGame, ChatGameException>> commandSupplier) {
            AtomicReference<BotApiMethod.BotApiMethodBuilder<?, ?, ?>> reply = new AtomicReference<>();
            commandSupplier.get().ifLeftOrElse(l -> reply.set(SendMessage.builder().chatId(l.getChatId())
                            .text(String.format("Enter a %d digit number", l.getComplexity()))),
                    r -> reply.set(SendMessage.builder().chatId(r.getChatId())
                            .text(r.getMessage())));
            return reply.get();
        }
    },
    INFO,
    SCORE,
    DELETE {
        @Override
        public BotApiMethod.BotApiMethodBuilder<?, ?, ?> getReply(Supplier<Either<IChatGame, ChatGameException>> commandSupplier) {
            AtomicReference<BotApiMethod.BotApiMethodBuilder<?, ?, ?>> reply = new AtomicReference<>();
            commandSupplier.get().ifLeftOrElse(l -> reply.set(DeleteMessage.builder().chatId(l.getChatId())
                            .messageId(l.getLastMessageId())),
                    r -> reply.set(SendMessage.builder().chatId(r.getChatId())
                            .text(r.getMessage())));
            return reply.get();
        }
    },

    REPLACE_MESSAGE {
        @Override
        public BotApiMethod.BotApiMethodBuilder<?, ?, ?> getReply(Supplier<Either<IChatGame, ChatGameException>> commandSupplier) {
            AtomicReference<BotApiMethod.BotApiMethodBuilder<?, ?, ?>> reply = new AtomicReference<>();
            commandSupplier.get().ifLeftOrElse(l -> reply.set(DeleteMessage.builder().chatId(l.getChatId())
                            .messageId(l.getLastMessageId())),
                    r -> reply.set(SendMessage.builder().chatId(r.getChatId())
                            .text(r.getMessage())));
            return reply.get();
        }
    },
    UNKNOWN {
        @Override
        public BotApiMethod.BotApiMethodBuilder<?, ?, ?> getReply(Supplier<Either<IChatGame, ChatGameException>> commandSupplier) {
            ChatGameException ex = commandSupplier.get().getRight().orElseThrow();
            return SendMessage.builder().chatId(ex.getChatId()).text(ex.getMessage());
        }

        public boolean isUnknown() {
            return true;
        }
    };

    public static GameCommand fromString(String str) {
        if (str == null || str.length() < 2) {
            return GameCommand.UNKNOWN;
        }

        var cmd = Arrays.stream(GameCommand.values())
                .filter(command -> command.toString().equals(str))
                .findFirst().orElse(GameCommand.UNKNOWN);

        if (cmd.isUnknown()) {
            log.warn("unknown command {}", str);
        }
        return cmd;
    }

    @Override
    public String toString() {
        return String.format("/%s", name().toLowerCase());
    }

    @Override
    public boolean isUnknown() {
        return false;
    }

    @Override
    public BotApiMethod.BotApiMethodBuilder<?, ?, ?> getReply(Supplier<Either<IChatGame, ChatGameException>> commandSupplier) {
        // TODO
        return null;
    }

}
