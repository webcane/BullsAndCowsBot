package cane.brothers.tgbot.emoji;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

public enum GameEmoji {
    // digits
    DIGIT_ZERO("30E283A3"),
    DIGIT_ONE("31E283A3"),
    DIGIT_TWO("32E283A3"),
    DIGIT_THREE("33E283A3"),
    DIGIT_FOUR("34E283A3"),
    DIGIT_FIVE("35E283A3"),
    DIGIT_SIX("36E283A3"),
    DIGIT_SEVEN("37E283A3"),
    DIGIT_EIGHT("38E283A3"),
    DIGIT_NINE("39E283A3"),
    // ox
    BULL("F09F9082"),
    // cow
    COW("F09F9084");

    final byte[] bytes;


    GameEmoji(String hex) {
        this.bytes = HexFormat.of().parseHex(hex);
    }

    public static GameEmoji getDigit(int ordinal) {
        if (ordinal >= 0 && ordinal < 10) {
            return GameEmoji.values()[ordinal];
        }
        throw new IllegalArgumentException("The ordinal should fall within the range from 0 to 9.");
    }

    @Override
    public String toString() {
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
