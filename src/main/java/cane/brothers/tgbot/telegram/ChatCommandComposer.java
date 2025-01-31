package cane.brothers.tgbot.telegram;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

enum ChatCommandComposer implements ICommandComposer {
    NEW {
        @Override
        public Deque<IChatCommand> getCommands() {
            var deq = new LinkedList<IChatCommand>();
            deq.add(GameCommand.NEW);
            return deq;
        }
    },
    INFO {
        @Override
        public Deque<IChatCommand> getCommands() {
            var deq = new LinkedList<IChatCommand>();
            deq.add(ReplyCommand.INFO);
            return deq;
        }
    },
    SCORE {
        @Override
        public Deque<IChatCommand> getCommands() {
            var deq = new LinkedList<IChatCommand>();
            deq.add(GameCommand.SCORE);
            return deq;
        }
    },
    SETTINGS {
        @Override
        public Deque<IChatCommand> getCommands() {
            var deq = new LinkedList<IChatCommand>();
            deq.add(ReplyCommand.SETTINGS);
            return deq;
        }
    },
    COMPLEXITY {
        @Override
        public Deque<IChatCommand> getCommands() {
            var deq = new LinkedList<IChatCommand>();
            deq.add(SettingsCommand.CALLBACK_COMPLEXITY);
            deq.add(SettingsCommand.CALLBACK_FINISH_GAME);
            deq.add(ReplyCommand.CALLBACK_HIDE_SETTINGS);
            deq.add(GameCommand.CALLBACK_NEW_GAME_WARN);
            return deq;
        }
    },
    MENU_SETTINGS {
        @Override
        public Deque<IChatCommand> getCommands() {
            var deq = new LinkedList<IChatCommand>();
            deq.add(ReplyCommand.CALLBACK_ANSWER);
            deq.add(ReplyCommand.CALLBACK_SETTINGS_TEXT);
            deq.add(ReplyCommand.CALLBACK_SETTINGS_REPLY_MARKUP);
            return deq;
        }
    },
    MENU_COMPLEXITY {
        @Override
        public Deque<IChatCommand> getCommands() {
            var deq = new LinkedList<IChatCommand>();
            deq.add(ReplyCommand.CALLBACK_ANSWER);
            deq.add(ReplyCommand.CALLBACK_COMPLEXITY_TEXT);
            deq.add(ReplyCommand.CALLBACK_COMPLEXITY_REPLY_MARKUP);
            return deq;
        }
    },
    MENU_REPLACE_MESSAGE {
        @Override
        public Deque<IChatCommand> getCommands() {
            var deq = new LinkedList<IChatCommand>();
            deq.add(ReplyCommand.CALLBACK_ANSWER);
            deq.add(SettingsCommand.CALLBACK_REPLACE_MESSAGE);
            deq.add(ReplyCommand.CALLBACK_HIDE_SETTINGS);
            // TODO message settings were updated
            return deq;
        }
    },
    MENU_SHOW_TURNS {
        @Override
        public Deque<IChatCommand> getCommands() {
            var deq = new LinkedList<IChatCommand>();
            deq.add(ReplyCommand.CALLBACK_ANSWER);
            deq.add(SettingsCommand.CALLBACK_SHOW_TURNS);
            deq.add(ReplyCommand.CALLBACK_HIDE_SETTINGS);
            // TODO message settings were updated
            return deq;
        }
    },
    MENU_HIDE_SETTINGS {
        @Override
        public Deque<IChatCommand> getCommands() {
            var deq = new LinkedList<IChatCommand>();
            deq.add(ReplyCommand.CALLBACK_ANSWER);
            deq.add(ReplyCommand.CALLBACK_HIDE_SETTINGS);
            return deq;
        }
    },
    UNKNOWN {
        @Override
        public Deque<IChatCommand> getCommands() {
            // no commands
            return new LinkedList<>();
        }

        @Override
        public boolean isUnknown() {
            return true;
        }
    };

    public static ChatCommandComposer fromString(String message) {
        if (message == null || message.length() < 2 || !message.startsWith("/")) {
            return ChatCommandComposer.UNKNOWN;
        }

        return Arrays.stream(ChatCommandComposer.values())
                .filter(command -> command.toString().equals(getMessageCommand(message)))
                .findFirst().orElse(ChatCommandComposer.UNKNOWN);
    }

    private static String getMessageCommand(String message) {
        var msg = message.split("=");
        return msg[0];
    }

    @Override
    public String toString() {
        return "/" + name().toLowerCase();
    }
}
