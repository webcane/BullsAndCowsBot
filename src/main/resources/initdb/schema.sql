CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS chat_game (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    chat_id BIGINT NOT NULL UNIQUE,
    last_message_id INTEGER,
    replace_message boolean,
    debug boolean DEFAULT FALSE,
    version INT NOT NULL
);

CREATE TABLE IF NOT EXISTS guess_game (
    game_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    complexity INTEGER CHECK (complexity > 0 AND complexity < 10),
    secret INTEGER[],
    ordinal INTEGER NOT NULL CHECK (ordinal >= 0),
    chat_game UUID NOT NULL REFERENCES chat_game(id)
);

CREATE TABLE IF NOT EXISTS guess_turn (
    turn_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    guess INTEGER[],
    bulls INTEGER CHECK (bulls >= 0),
    cows INTEGER CHECK (cows >= 0),
    move_time TIMESTAMPTZ DEFAULT now(),
    ordinal INTEGER NOT NULL CHECK (ordinal >= 0),
    guess_game UUID NOT NULL REFERENCES guess_game(game_id)
);
