package DataBase.dao;

import DataBase.Entity.User;
import DataBase.Helper.UserHelper;

import java.util.List;
import java.util.Optional;

public class UsersDAO {
    private static UserHelper userHelper = new UserHelper();

    //Этот булевый метод проверяет, есть ли пользователь в базе данных
    public static boolean userMathInDb(Long chatId) {
        List<User> users = userHelper.getUserByOptionalFieldList(
                Optional.of("id"), // У тебя в классе User поле называется "id", а не "tg_chat_id"
                Optional.ofNullable(chatId)
        );

        if (users.isEmpty()) {
            return false;
        }

        User user = users.get(0); // Берем первого
        return user.getId().equals(chatId);
    }

    //Данный метод добавляет телеграм-пользователя в базу данных
    public static void setUser(Long chat_id, String userName) {
        userHelper.setUserToDB(chat_id, userName);
    }
}
