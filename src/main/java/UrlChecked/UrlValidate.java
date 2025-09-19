package UrlChecked;

import telegram.TgBot;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


import static DataBase.dao.StreamerDAO.getStreamers;
import static DataBase.dao.StreamerDAO.setStreamer;
import static DataBase.dao.StreamerToUsersDAO.deleteStreamerAtUser;
import static DataBase.dao.StreamerToUsersDAO.setUserOnStreamer;
import static twitch.TwitchApi.getTwitchId;

//Данный класс нужен для валидации ссылок
public class UrlValidate {

    //Список всех символов, которые могут быть
    private static final String URL_REGEX =
            "^(https?|ftp)://[a-zA-Z0-9\\-_.]+\\.[a-zA-Z]{2,}(:\\d+)?(/.*)?$";

    //Паттерн, которому должны следовать url ссылки
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    //Метод для проверки сообщения на пример того, а является ли оно ссылкой
    public static boolean validateUrl(String urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {      //Если строка пустая, или остаётся пустой после удаления пробелов
            return false;       //Возвращает false
        }

        // Проверка по регулярке
        if (!URL_PATTERN.matcher(urlString).matches()) {        //Проверяем, соответствует ли вс строка(текст) паттерну. Если нет, то возвращаем false
            return false;
        }

        //Проверка username через twitchApi
        if (getTwitchId(extractStreamerId(urlString)) == null) {        //Если username не находится через twitchApi, возвращаем false
            return false;
        }

        // Дополнительная проверка через java.net.URL
        try {
            new URL(urlString);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    //Данный метод извлекает username стримера из ссылки
    public static String extractStreamerId(String urlString) {
        try {
            URL url = new URL(urlString);
            String[] parts = url.getPath().split("/"); // Разбиваем путь по "/" и записываем в parts

            if (parts.length >= 2) {    //Если длина массива parts больше или равна 2
                return parts[1]; // ID стримера
            } else {
                return null;    //Возвращаем пустоту
            }
        } catch (Exception e) {
            System.out.println("❌ Ошибка обработки URL");       //Вывод в консоль, на случай ошибки
            return null;
        }
    }
}

