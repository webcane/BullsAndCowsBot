package cane.brothers.tgbot.telegram;

import cane.brothers.game.IGuessTurn;
import cane.brothers.tgbot.emoji.GameEmoji;
import cane.brothers.tgbot.game.ChatGameException;
import cane.brothers.tgbot.game.ChatGameService;
import cane.brothers.tgbot.game.ChatGameSettingsService;
import cane.brothers.tgbot.game.IChatGame;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
enum GameCommand implements IChatCommand<Message> {
    SCORE {
        @Override
        public void execute(Message message, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) {
            // TODO score
            log.info("show score");
        }
    },
    NEW {
        @Override
        public void execute(Message message, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            var chatId = message.getChatId();
            var chatGame = gameService.newGame(chatId);
            var reply = SendMessage.builder().chatId(chatId)
                    .text(String.format("Enter a %d digit number", chatGame.getComplexity())).build();
            telegramClient.execute(reply);
        }
    },

    SHOW_TURN_RESULTS {
        @Override
        public void execute(Message message, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            var chatId = message.getChatId();

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
                return getTurnLine(gameTurn) + "\n"
                        + (gameTurn.isWin() ? GameEmoji.HIT : "");
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

            var reply = SendMessage.builder().chatId(chatId)
                    .text(displayEmojiResult(chatGame)).build();
            telegramClient.execute(reply);
        }

        public String displayEmojiResult(@NotNull IChatGame chatGame) {
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
            var reply = SendMessage.builder().chatId(chatId)
                    .text("Please, start another game using /new command").build();

            var lastMethod = telegramClient.execute(reply);
            SAVE_LAST_MESSAGE.execute(lastMethod, gameService, gameSettings, telegramClient);
        }
    },

    DELETE_LAST_MESSAGE {
        @Override
        public void execute(Message message, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            var chatId = message.getChatId();
            var messageId = gameService.getChatGame(chatId).getLastMessageId();
            if (messageId != null) {
                var reply = DeleteMessage.builder().chatId(chatId)
                        .messageId(messageId)
                        .build();
                telegramClient.execute(reply);
            }
        }
    },

    SAVE_LAST_MESSAGE {
        @Override
        public void execute(Message message, ChatGameService gameService, ChatGameSettingsService gameSettings, TelegramClient telegramClient) throws TelegramApiException, ChatGameException {
            var chatId = message.getChatId();

            if (gameService.isInProgress(chatId)) {
                gameService.setLastMessageId(chatId, message.getMessageId());
            }
        }
    };

    @Override
    public String toString() {
        return String.format("/%s", name().toLowerCase());
    }
}
