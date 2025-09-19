package telegram;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.util.*;

//Данный класс нужен для стирания лишних сообщений после выполнения действий
public class BotCollector {

    //Создаём Hash map куда будем добавлять chatId и id сообщения
    private static final Map<Long, Deque<Integer>> userMessages = new HashMap<>();

    // Сохраняем ID отправленного сообщения и chatId пользователя
    public static void trackMessage(Long chatId, Message message) {
        userMessages.putIfAbsent(chatId, new ArrayDeque<>());

        Deque<Integer> messages = userMessages.get(chatId);
        messages.addLast(message.getMessageId());
    }

    // Получаем все отслеживаемые сообщения
    public Deque<Integer> getTrackedMessages(Long chatId) {
        return userMessages.getOrDefault(chatId, new ArrayDeque<>());
    }

    // Получаем список со всеми отслеживаемыми сообщениями
    public List<DeleteMessage> buildDeleteRequests(Long chatId) {
        List<DeleteMessage> requests = new ArrayList<>();       //Создаём лист, куда запишем все сообщения конкретного пользователя
        for (int messageId : getTrackedMessages(chatId)) {      //При помощи перебора, добавляем в лист все отслеживаемые сообщения пользователя
            requests.add(new DeleteMessage(chatId.toString(), messageId));
        }
        return requests;        //Возвращаем список
    }

    // Очистить отслеживание сообщений
    public void clear(Long chatId) {
        userMessages.remove(chatId);
    }
}

