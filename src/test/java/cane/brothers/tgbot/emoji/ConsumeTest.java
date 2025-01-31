package cane.brothers.tgbot.emoji;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Function;

public class ConsumeTest {

    @Test
    void testConsumer() {
        Consumer<String> first = x -> System.out.println(x.toLowerCase());
        Consumer<String> second = y -> System.out.println("aaa " + y);
        Consumer<String> consumer = first.andThen(second);
        consumer.accept("Java"); // java, aaa Java
    }

    @Test
    void testFunction() {
        Function<String, String> first = x -> x + "1";
        Function<String, String> second = y -> y + "2";
        Function<String, String> consumer = first.andThen(second);
        var result = consumer.apply("Java");
        Assertions.assertEquals(result, "Java12");
    }

    interface ICommand {
        String execute(Integer id);
    }

    enum Command implements Consumer<Command>, ICommand {
        CMD1,
        CMD2;

        @Override
        public void accept(Command command) {
            command.execute(command.ordinal());
        }

        @Override
        public String execute(Integer id) {
            return String.valueOf(id);
        }
    }


}
