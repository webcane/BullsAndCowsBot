package cane.brothers.tgbot.telegram;

import cane.brothers.game.IGuessTurn;
import cane.brothers.tgbot.emoji.GameEmoji;
import cane.brothers.tgbot.game.ChatGameException;
import cane.brothers.tgbot.game.IChatGame;
import io.jbock.util.Either;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Slf4j
enum GameCommand implements IGameCommand {
    NEW {
        @Override
        public BotApiMethod<? extends Serializable> getReply(Supplier<Either<IChatGame, ChatGameException>> commandSupplier) {
            AtomicReference<BotApiMethod.BotApiMethodBuilder<?, ?, ?>> reply = new AtomicReference<>();
            commandSupplier.get().ifLeftOrElse(l -> reply.set(SendMessage.builder().chatId(l.getChatId())
                            .text(String.format("Enter a %d digit number", l.getComplexity()))),
                    r -> reply.set(SendMessage.builder().chatId(r.getChatId())
                            .text(r.getMessage())));
            return reply.get().build();
        }
    },
    INFO,
    SCORE,
    DELETE {
        @Override
        public BotApiMethod<? extends Serializable> getReply(Supplier<Either<IChatGame, ChatGameException>> commandSupplier) {
            AtomicReference<BotApiMethod.BotApiMethodBuilder<?, ?, ?>> reply = new AtomicReference<>();
            commandSupplier.get().ifLeftOrElse(l -> reply.set(DeleteMessage.builder().chatId(l.getChatId())
                            .messageId(l.getLastMessageId())),
                    r -> reply.set(SendMessage.builder().chatId(r.getChatId())
                            .text(r.getMessage())));
            return reply.get().build();
        }
    },

    REPLACE_MESSAGE,
    SHOW_LAST_TURN_RESULT {
        @Override
        public BotApiMethod<? extends Serializable> getReply(Supplier<Either<IChatGame, ChatGameException>> commandSupplier) {
            AtomicReference<SendMessage.SendMessageBuilder<?, ?>> reply = new AtomicReference<>();
            commandSupplier.get().ifLeftOrElse(
                    chatGame -> reply.set(SendMessage.builder().chatId(chatGame.getChatId()).text(displayEmojiResult(chatGame))),
                    r -> reply.set(SendMessage.builder().chatId(r.getChatId()).text(r.getMessage())));
            return reply.get().build();
        }

        public String displayEmojiResult(IChatGame chatGame) {
            var gameTurn = chatGame.getCurrentGame().getTurns().getLast();
            return getTurnLine(gameTurn) + "\n"
                    + (gameTurn.isWin() ? GameEmoji.HIT : "");
        }

        private String getTurnLine(IGuessTurn gameTurn) {
            return "" + GameEmoji.getDigit(gameTurn.getBulls()) + GameEmoji.BULL +
                    GameEmoji.getDigit(gameTurn.getCows()) + GameEmoji.COW;
        }
    },
    SHOW_ALL_TURNS_RESULT {
        @Override
        public BotApiMethod<? extends Serializable> getReply(Supplier<Either<IChatGame, ChatGameException>> commandSupplier) {
            AtomicReference<SendMessage.SendMessageBuilder<?, ?>> reply = new AtomicReference<>();
            commandSupplier.get().ifLeftOrElse(
                    chatGame -> reply.set(SendMessage.builder().chatId(chatGame.getChatId()).text(displayEmojiResult(chatGame))),
                    r -> reply.set(SendMessage.builder().chatId(r.getChatId()).text(r.getMessage())));
            return reply.get().build();
        }

        public String displayEmojiResult(IChatGame chatGame) {
            // TODO table
            var allTurns = chatGame.getCurrentGame().getTurns();
            StringBuilder sb = new StringBuilder();
            int ordinal = 1;
            for (var turn : allTurns) {
                sb.append(ordinal++);
                sb.append(".");
                sb.append("\t\t\t\t");
                appendGuess(turn, sb);
                sb.append("\t\t\t\t");
                sb.append(getTurnLine(turn)).append("\n");
            }
            return sb.toString();
        }

        private String getTurnLine(IGuessTurn gameTurn) {
            return "" + GameEmoji.getDigit(gameTurn.getBulls()) + GameEmoji.BULL +
                    GameEmoji.getDigit(gameTurn.getCows()) + GameEmoji.COW;
        }

        private void appendGuess(IGuessTurn guessTurn, StringBuilder sb) {
            for (int d : guessTurn.getDigits()) {
                sb.append(d);
            }
        }
    },
    SHOW_WIN_RESULT {
        @Override
        public BotApiMethod<? extends Serializable> getReply(Supplier<Either<IChatGame, ChatGameException>> commandSupplier) {
            AtomicReference<SendMessage.SendMessageBuilder<?, ?>> reply = new AtomicReference<>();
            commandSupplier.get().ifLeftOrElse(
                    chatGame -> reply.set(SendMessage.builder().chatId(chatGame.getChatId()).text(displayEmojiResult(chatGame))),
                    r -> reply.set(SendMessage.builder().chatId(r.getChatId()).text(r.getMessage())));
            return reply.get().build();
        }

        public String displayEmojiResult(IChatGame chatGame) {
            var guessGame = chatGame.getCurrentGame();
            StringBuilder sb = new StringBuilder();
            if (guessGame.isWin()) {
                sb.append(GameEmoji.HIT);
            }
            return sb.toString();
        }
    },
    SHOW_WIN_MESSAGE {
        @Override
        public BotApiMethod<? extends Serializable> getReply(Supplier<Either<IChatGame, ChatGameException>> commandSupplier) {
            AtomicReference<SendMessage.SendMessageBuilder<?, ?>> reply = new AtomicReference<>();
            commandSupplier.get().ifLeftOrElse(
                    chatGame -> reply.set(SendMessage.builder().chatId(chatGame.getChatId()).text(displayEmojiResult(chatGame))),
                    r -> reply.set(SendMessage.builder().chatId(r.getChatId()).text(r.getMessage())));
            return reply.get().build();
        }

        public String displayEmojiResult(IChatGame chatGame) {
            var guessGame = chatGame.getCurrentGame();
            if (guessGame.isWin()) {
                StringBuilder sb = new StringBuilder("You are win! Secret guess is ");
                appendGuess(guessGame.getTurns().getLast(), sb);
                sb.append(". Number of turns is ");
                sb.append(guessGame.getTurns().size());
                return sb.toString();
            }
            return "";
        }

        private void appendGuess(IGuessTurn guessTurn, StringBuilder sb) {
            for (int d : guessTurn.getDigits()) {
                sb.append(d);
            }
        }
    },
    NEW_GAME_WARN {
        @Override
        public BotApiMethod<? extends Serializable> getReply(Supplier<Either<IChatGame, ChatGameException>> commandSupplier) {
            var chatGame = commandSupplier.get().getLeft().orElseThrow();
            return SendMessage.builder().text("Please, start another game using /new command")
                    .chatId(chatGame.getChatId()).build();
        }
    },
    UNKNOWN {
        @Override
        public BotApiMethod<? extends Serializable> getReply(Supplier<Either<IChatGame, ChatGameException>> commandSupplier) {
            ChatGameException ex = commandSupplier.get().getRight().orElseThrow();
            return SendMessage.builder().chatId(ex.getChatId()).text(ex.getMessage()).build();
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
    public BotApiMethod<? extends Serializable> getReply(Supplier<Either<IChatGame, ChatGameException>> commandSupplier) {
        // do not send any telegram messages
        return null;
    }

}
