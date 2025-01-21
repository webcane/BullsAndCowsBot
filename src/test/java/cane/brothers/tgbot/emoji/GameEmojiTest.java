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

    @Test
    void test_array() {
        int[] digits = new int[4];
        Assertions.assertEquals(0, digits.length);
    }

    @Test
    void test_array_zero_value() {
        int[] digits = new int[4];
        Assertions.assertEquals(0, digits[0]);
    }


}