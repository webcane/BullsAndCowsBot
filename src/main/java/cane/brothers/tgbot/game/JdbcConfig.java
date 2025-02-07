package cane.brothers.tgbot.game;

import cane.brothers.game.IGuessNumber;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.mapping.JdbcValue;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
class JdbcConfig extends AbstractJdbcConfiguration {

    private final ArrayToGuessNumberConverter arrayToGuessNumberConverter;

    @Bean
    DataSourceInitializer initializer(DataSource dataSource) {
        var initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);

        var script = new ClassPathResource("initdb/schema.sql");
        var populator = new ResourceDatabasePopulator(script);
        initializer.setDatabasePopulator(populator);

        return initializer;
    }


    @NotNull
    @Override
    protected List<?> userConverters() {
        return Arrays.asList(arrayToGuessNumberConverter,
                new GuessNumberWritingConverter(),
                new UUIDWritingConverter());
    }

    @WritingConverter
    static class GuessNumberWritingConverter implements Converter<IGuessNumber, JdbcValue> {

        @NotNull
        @Override
        public JdbcValue convert(@NotNull IGuessNumber source) {
            return JdbcValue.of(source.getDigits(), new GuessNumberSQLType());
        }
    }

    @WritingConverter
    static class UUIDWritingConverter implements Converter<UUID, JdbcValue> {
        @NotNull
        @Override
        public JdbcValue convert(@NotNull UUID source) {
            return JdbcValue.of(source, new UUIDSQLType());
        }
    }
}
