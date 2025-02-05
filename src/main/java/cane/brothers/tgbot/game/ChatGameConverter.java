package cane.brothers.tgbot.game;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ChatGameConverter implements Converter<ChatGame, IChatGame> {

    private final ConversionService conversionSvc;

    @NotNull
    @Override
    public IChatGame convert(@NotNull ChatGame source) {
        return new IChatGame() {
            @Override
            public Long getChatId() {
                return source.getChatId();
            }

            @Override
            public Integer getLastMessageId() {
                return source.getLastMessageId();
            }

            public int getComplexity() {
                return source.getCurrentGame() == null ? -1 : source.getCurrentGame().getComplexity();
            }

            @Override
            public IGuessGame getCurrentGame() {
                return conversionSvc.convert(source.getCurrentGame(), IGuessGame.class);
            }
        };
    }
}
