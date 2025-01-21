package cane.brothers.tgbot.emoji;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

// https://apps.timwhitlock.info/emoji/tables/unicode
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
    COW("F09F9084"),
    // warning sign
    WARN("E29AA0"),
    // victory hand
    VICTORY_HAND("E29C8C"),
    // glowing star
    STAR("F09F8C9F"),
    // wrapped present
    PRESENT("F09F8E81"),
    // party popper
    POPPER("F09F8E89"),
    // direct hit
    HIT("F09F8EAF"),
    // trophy
    TROPHY("F09F8F86"),
    // clapping hands sign
    CLAPPING_HANDS("F09F918F"),
    // crown
    CROWN("F09F9191"),
    // flexed biceps
    BICEPS("F09F92AA"),
    // hundred points symbol
    HUNDRED("F09F92AF"),
    // money bag
    MONEY_BAG("F09F92B0"),
    // money with wings
    MONEY_WINGS("F09F92B8"),
    // fire
    FIRE("F09F94A5");

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
