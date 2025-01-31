package cane.brothers.tgbot.telegram;

import cane.brothers.tgbot.game.ChatGameException;
import cane.brothers.tgbot.game.ChatGameService;
import cane.brothers.tgbot.game.ChatGameSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Slf4j
enum SettingsCommand implements ISettingsCommand {
    CALLBACK_COMPLEXITY {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings) throws ChatGameException {
            var msg = callbackQuery.getData().split("=");

            if (msg.length > 1) {
                var complexity = Integer.valueOf(msg[1]).intValue();
                log.info("set default game complexity to: " + complexity);
                gameSettings.setComplexity(callbackQuery.getMessage().getChatId(), complexity);
            } else {
                log.info("unknown settings: " + callbackQuery.getData());
            }

            //            deq.add(SettingsCommand.CALLBACK_FINISH_GAME);
            //            deq.add(ReplyCommand.CALLBACK_HIDE_SETTINGS);
            //            deq.add(GameCommand.CALLBACK_NEW_GAME_WARN);
        }
    },
    CALLBACK_FINISH_GAME {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings) throws ChatGameException {
            // TODO logging
            gameService.finishGame(callbackQuery.getMessage().getChatId());
        }
    },
    CALLBACK_REPLACE_MESSAGE {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings) throws ChatGameException {
            // TODO
//            ReplyCommand.CALLBACK_ANSWER.execute();
            gameSettings.updateReplaceMessage(callbackQuery.getMessage().getChatId());
//            ReplyCommand.CALLBACK_HIDE_SETTINGS.execute();
        }
    },
    CALLBACK_SHOW_TURNS {
        @Override
        public void execute(CallbackQuery callbackQuery, ChatGameService gameService, ChatGameSettingsService gameSettings) throws ChatGameException {
            gameSettings.updateShowAllTurns(callbackQuery.getMessage().getChatId());
        }
    };

    @Override
    public String toString() {
        return String.format("/%s", name().toLowerCase());
    }

}
