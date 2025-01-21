package cane.brothers.tgbot.game;

import cane.brothers.game.IGuessNumber;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.convert.ReadingConverter;
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
class JdbcConfig extends AbstractJdbcConfiguration {

    @Bean
    DataSourceInitializer initializer(DataSource dataSource) {
        var initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);

        var script = new ClassPathResource("initdb/schema.sql");
        var populator = new ResourceDatabasePopulator(script);
        initializer.setDatabasePopulator(populator);

        return initializer;
    }


    @Override
    protected List<?> userConverters() {
        return Arrays.asList(new ArrayToGuessNumberConverter(),
                new GuessNumberWritingConverter(),
                new UUIDWritingConverter());
    }

    @ReadingConverter
    static class ArrayToGuessNumberConverter implements Converter<Integer[], IGuessNumber> {

        @Override
        public IGuessNumber convert(Integer[] source) {
            int[] d = ArrayUtils.toPrimitive(source);
            return new GuessNumber(d);
        }
    }

    @WritingConverter
    static class GuessNumberWritingConverter implements Converter<IGuessNumber, JdbcValue> {

        @Override
        public JdbcValue convert(IGuessNumber source) {
            return JdbcValue.of(source.getDigits(), new GuessNumberSQLType());
        }
    }

    @WritingConverter
    static class UUIDWritingConverter implements Converter<UUID, JdbcValue> {
        @Override
        public JdbcValue convert(UUID source) {
            return JdbcValue.of(source, new UUIDSQLType());
        }
    }
}
