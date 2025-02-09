package cane.brothers.tgbot.telegram;

import cane.brothers.game.IGuessTurn;
import cane.brothers.tgbot.emoji.GameEmoji;
import cane.brothers.tgbot.game.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Comparator;

@Slf4j
enum GameCommand implements IChatCommand<Message>, Utils {
    SCORE {
        @Override
        public void execute(Message message, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws ChatGameException, TelegramApiException {
            var chatId = message.getChatId();
            log.debug("Show score for chat %d".formatted(chatId));

            var allGames = gameService.getAllGames(chatId);
            var gamesNumber = allGames.size();
            var minTurns = allGames.stream()
                    .filter(IGuessGame::isWin)
                    .map(gg -> gg.getTurns().size())
                    .min(Comparator.comparingInt(gg -> gg))
                    .orElse(0);
            var gamesWin = allGames.stream().filter(IGuessGame::isWin).count();
            var reply = SendMessage.builder().chatId(chatId)
                    .parseMode(ParseMode.MARKDOWNV2)
                    .text(String.format("*Game Score*\n\nNumber of wins: *%d*\nNumber of games played: *%d*\nMinimum number of turns: *%d*",
                            gamesWin, gamesNumber, minTurns)).build();

            var lastMethod = telegramClient.execute(reply);
            SAVE_LAST_MESSAGE.execute(lastMethod, gameService, gameSettings, telegramClient);
        }
    },
    START {
        @Override
        public void execute(Message message, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            var chatId = message.getChatId();
            log.info("Start new game for chat %d".formatted(chatId));

            var chatGame = gameService.newGame(chatId);
            var reply = SendMessage.builder().chatId(chatId)
                    .parseMode(ParseMode.MARKDOWNV2)
                    .text(String.format("Enter a *%d* digit number", chatGame.getComplexity())).build();

            var lastMethod = telegramClient.execute(reply);
            SAVE_LAST_MESSAGE.execute(lastMethod, gameService, gameSettings, telegramClient);
        }
    },

    SHOW_TURN_RESULTS {
        @Override
        public void execute(Message message, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            var chatId = message.getChatId();
            log.debug("Show turn results for chat %d".formatted(chatId));

            if (gameSettings.isReplaceMessage(chatId)) {
                GameCommand.DELETE_LAST_MESSAGE.execute(message, gameService, gameSettings, telegramClient);
            }

            var chatGame = gameService.makeTurn(chatId, message.getText());

            var reply = SendMessage.builder().chatId(chatId)
                    .text(displayEmojiResult(chatGame, gameSettings.isShowAllTurns(chatId)))
                    .build();

            var lastMethod = telegramClient.execute(reply);
            GameCommand.SAVE_LAST_MESSAGE.execute(lastMethod, gameService, gameSettings, telegramClient);

            // show win
            if (gameService.isWin(chatId)) {
                log.info("Show win results for chat %d".formatted(chatId));
                GameCommand.SHOW_WIN_RESULT.execute(message, gameService, gameSettings, telegramClient);
                GameCommand.SHOW_WIN_MESSAGE.execute(message, gameService, gameSettings, telegramClient);

                // remove last message id
                gameService.setLastMessageId(chatId, null);
            }
        }

        public String displayEmojiResult(IChatGame chatGame, boolean showAllTurns) {
            if (showAllTurns) {
                // SHOW_ALL_TURNS_RESULT
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
            } else {
                // SHOW_LAST_TURN_RESULT
                var gameTurn = chatGame.getCurrentGame().getTurns().getLast();
                return getTurnLine(gameTurn) + "\n";
            }
        }

        @NotNull
        private String getTurnLine(@NotNull IGuessTurn gameTurn) {
            return "" + GameEmoji.getDigit(gameTurn.getBulls()) + GameEmoji.BULL +
                    GameEmoji.getDigit(gameTurn.getCows()) + GameEmoji.COW;
        }

        private void appendGuess(@NotNull IGuessTurn guessTurn, StringBuilder sb) {
            for (int d : guessTurn.getDigits()) {
                sb.append(d);
            }
        }
    },

    SHOW_WIN_RESULT {
        @Override
        public void execute(Message message, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            var chatId = message.getChatId();
            var chatGame = gameService.getChatGame(chatId);

            var reply = SendMessage.builder().chatId(chatId)
                    .text(displayEmojiResult(chatGame)).build();
            telegramClient.execute(reply);
        }

        public String displayEmojiResult(@NotNull IChatGame chatGame) {
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
        public void execute(Message message, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            var chatId = message.getChatId();
            var chatGame = gameService.getChatGame(chatId);
            log.info("Number of turns %d".formatted(chatGame.getCurrentGame().getTurns().size()));

            var reply = SendMessage.builder().chatId(chatId)
                    .parseMode(ParseMode.MARKDOWNV2)
                    .text(escape(displayEmojiResult(chatGame))).build();
            telegramClient.execute(reply);
        }

        public String displayEmojiResult(@NotNull IChatGame chatGame) {
            var guessGame = chatGame.getCurrentGame();
            if (guessGame.isWin()) {
                StringBuilder sb = new StringBuilder("You are win! Secret guess is *");
                appendGuess(guessGame.getTurns().getLast(), sb);
                sb.append("*. Number of turns is *");
                sb.append(guessGame.getTurns().size());
                sb.append("*");
                return sb.toString();
            }
            return "";
        }

        private void appendGuess(@NotNull IGuessTurn guessTurn, StringBuilder sb) {
            for (int d : guessTurn.getDigits()) {
                sb.append(d);
            }
        }
    },

    NEW_GAME_WARN {
        @Override
        public void execute(Message message, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            var chatId = message.getChatId();
            log.info("Ask new game warning for chat %d".formatted(chatId));

            var reply = SendMessage.builder().chatId(chatId)
                    .text("Please, start another game using /start command")
                    .replyMarkup(new ReplyKeyboardRemove(true))
                    .build();

            var lastMethod = telegramClient.execute(reply);
            SAVE_LAST_MESSAGE.execute(lastMethod, gameService, gameSettings, telegramClient);
        }
    },

    DELETE_LAST_MESSAGE {
        @Override
        public void execute(Message message, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws ChatGameException {
            var chatId = message.getChatId();
            var messageId = gameService.getChatGame(chatId).getLastMessageId();
            log.debug("Delete message %d for chat %d".formatted(messageId, chatId));

            if (messageId != null) {
                try {
                    var reply = DeleteMessage.builder().chatId(chatId)
                            .messageId(messageId)
                            .build();
                    telegramClient.execute(reply);
                } catch (TelegramApiException tex) {
                   log.warn(tex.getMessage());
                }
            }
        }
    },

    SAVE_LAST_MESSAGE {
        @Override
        public void execute(Message message, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            var chatId = message.getChatId();
            var messageId = message.getMessageId();
            log.debug("Save last message id %d for chat %d".formatted(messageId, chatId));

            if (gameService.isInProgress(chatId)) {
                gameService.setLastMessageId(chatId, messageId);
            }
        }
    };

    @Override
    public String toString() {
        return String.format("/%s", name().toLowerCase());
    }
}
