package InteractionUserAndStreamer;

import java.util.HashMap;
import java.util.Map;

public class AddAndDeleteFlagList {
    private static AddAndDeleteFlagList instance;
    /*Данные HashMap хранят действие пользователя.
    Ссылка будет обрабатываться только если пользователь есть в одном из двух HashMap */
    private final Map<Long, Boolean> waitingAddForLinks = new HashMap<>();       //HashMap пользователей на добавление стримера
    private final Map<Long, Boolean> waitingDeleteForLinks = new HashMap<>();        //HashMap пользователей на удаление стримера

    public AddAndDeleteFlagList() {
    }

    public static synchronized AddAndDeleteFlagList getInstance() {
        if (instance == null) {
            instance = new AddAndDeleteFlagList();
        }
        return instance;
    }

    //Проверяем наличие пользователя в листе удаления
    public boolean checkStateDeleteList(Long chatId) {
        return waitingDeleteForLinks.containsKey(chatId);
    }

    //Проверяем наличие пользователя в листе добавления
    public boolean checkStateAddList(Long chatId) {
        return waitingAddForLinks.containsKey(chatId);
    }

    //Метод, для очистки флаг-листа удаления от пользователя
    public void clearWaitListDelete(Long chatId) {
        if (waitingDeleteForLinks.containsKey(chatId)) {
            System.out.println("Пользователь убран из линка удаления");
            waitingDeleteForLinks.remove(chatId);
        }
    }

    //Метод, для очистки флаг-листа добавления от пользователя
    public void clearWaitListAdd(Long chatId) {
        if (waitingAddForLinks.containsKey(chatId)) {
            System.out.println("Пользователь убран из линка добавления");
            waitingAddForLinks.remove(chatId);
        }
    }

    public void addUserToDeleteList(Long chatId) {
        waitingDeleteForLinks.put(chatId, true);
    }

    public void addUserToAddList(Long chatId) {
        waitingAddForLinks.put(chatId, true);
    }
}
