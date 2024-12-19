package cane.brothers.tgbot.emoji;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class GameEmojiTest {

    @Test
    void testToString() {
    }

    @Test
    void test_value() {
        Assertions.assertEquals(0, GameEmoji.DIGIT_ZERO.ordinal());
        Assertions.assertEquals(1, GameEmoji.DIGIT_ONE.ordinal());
    }
}