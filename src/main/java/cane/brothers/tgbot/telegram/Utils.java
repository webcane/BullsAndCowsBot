package cane.brothers.tgbot.telegram;

import cane.brothers.tgbot.App;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.LookupTranslator;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface Utils {
    Map<CharSequence, CharSequence> ESCAPE_MAP = new HashMap<>() {
        {
            put(".", "\\.");
            put("!", "\\!");
        }
    };
    AggregateTranslator ESCAPE = new AggregateTranslator(
            new LookupTranslator(Collections.unmodifiableMap(ESCAPE_MAP))
    );

    default String escape(String input) {
        return StringEscapeUtils.builder(ESCAPE).escape(input).toString();
    }

    default String readMarkDownFile(String fileName) {
        ClassPathResource res = new ClassPathResource("/" + fileName, App.class);
        try (InputStream in = res.getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return String.format("Can't find %s file", fileName);
        }
    }
}
