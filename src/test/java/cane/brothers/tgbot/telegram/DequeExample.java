package cane.brothers.tgbot.telegram;

import java.util.ArrayDeque;
import java.util.Deque;

public class DequeExample {
    public static void main(String[] args) {
        Deque<String> deque = new ArrayDeque<>();
        deque.add("Element 1");
        deque.add("Element 2");
        deque.add("Element 3");

        for (String element : deque) {
            System.out.println(element);
        }

//        Deque<ChatCommand> deque = new ChatCommand("answer_callback_query", CommandType.CALLBACK_MESSAGE)
//                .andThen(new ChatCommand("settings_text", CommandType.CALLBACK_MESSAGE))
//                .apply(new ChatCommand("settings_reply_markup", CommandType.CALLBACK_MESSAGE))
//                .getQueue();
//
//        Iterator<ChatCommand> iterator = deque.iterator();
//        while (iterator.hasNext()) {
//            ChatCommand element = iterator.next();
//            System.out.println(element);
//        }
    }
}
