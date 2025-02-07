package cane.brothers.tgbot.game;

import cane.brothers.game.IGuessNumber;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public class ArrayToGuessNumberConverter implements Converter<Integer[], IGuessNumber> {

    @NotNull
    @Override
    public IGuessNumber convert(@NotNull Integer[] source) {
        int[] d = ArrayUtils.toPrimitive(source);
        return new GuessNumber(d);
    }
}
