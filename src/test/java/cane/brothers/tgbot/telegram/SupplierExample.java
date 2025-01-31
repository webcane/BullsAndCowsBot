package cane.brothers.tgbot.telegram;

import java.util.function.Function;

public class SupplierExample {
    public static void main(String[] args) {
        // Исходный операнд
        //    Supplier<Integer> operandSupplier = () -> 5;

        // Функции для последовательной обработки
        Function<Integer, Integer> multiplyByTwo = (x) -> x * 2;
        Function<Integer, Integer> addTen = (x) -> x + 10;
        Function<Integer, Integer> square = (x) -> x * x;

        // Последовательное применение функций
        Integer result = square
                .compose(addTen)
                .compose(multiplyByTwo)
                .apply(5);

        System.out.println("Результат: " + result);
//        Deque<ChatCommand> deque = new ChatCommand("settings_reply_markup", CommandType.CALLBACK_MESSAGE)
//                .compose(new ChatCommand("settings_text", CommandType.CALLBACK_MESSAGE))
//                .apply(new ChatCommand("answer_callback_query", CommandType.CALLBACK_MESSAGE))
//                .getQueue();
//
//        Iterator<ChatCommand> iterator = deque.iterator();
//        while (iterator.hasNext()) {
//            ChatCommand element = iterator.next();
//            System.out.println(element);
//        }
    }
}
