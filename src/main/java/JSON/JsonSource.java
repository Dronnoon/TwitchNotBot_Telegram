package JSON;

import org.json.JSONObject;

import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class JsonSource {

    //В переменную присваиваем имя json-файла
    private static final String FILE_NAME = "source.json";

    //Создаём метод для чтения json-файла
    private static JSONObject readJson() {
        // Этот метод ищет json файл в папке resources
        InputStream inputStream = JsonSource.class.getClassLoader().getResourceAsStream(FILE_NAME);

        //Проверяем наличие файла
        if (inputStream == null) {
            System.out.println(FILE_NAME + " not found");
        }

        // Читаем файл в строку, используя кодировку UTF-8
        String jsonString = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();

        //Создаём json объект, и передаём в него данные.
        return new JSONObject(jsonString);
    }

    //Получаем массив объектов twitchKey, и возвращаем запрошенное
    private static String getValueTwitchKey(String key) {
        //В переменную jsonObject читаем файл
        JSONObject jsonObject = readJson();

        //Возвращаем значение по ключу
        return jsonObject.getJSONArray("twitchKey").getJSONObject(0).getString(key);
    }

    //Получаем массив объектов telegramKey, и возвращаем запрошенное
    private static String getValueTelegramKey(String key) {
        //В переменную jsonObject читаем файл
        JSONObject jsonObject = readJson();

        //Возвращаем значение по ключу
        return jsonObject.getJSONArray("telegramKey").getJSONObject(0).getString(key);
    }

    //Получаем массив объектов dataBase, и возвращаем запрошенное
    private static String getValueDBKey(String key) {
        //В переменную jsonObject читаем файл
        JSONObject jsonObject = readJson();

        //Возвращаем значение по ключу
        return jsonObject.getJSONArray("dataBase").getJSONObject(0).getString(key);
    }

    //Получить clientId
    public static String getClientId() {
        return getValueTwitchKey("clientId");
    }

    //получить clientSecret
    public static String getClientSecret() {
        return getValueTwitchKey("clientSecret");
    }


    //Получить oAuth2 токен
    public static String getOAuth2Token() {
        return getValueTwitchKey("OAuth2");
    }

    public static void setOAuthtoken(String token) {
        JSONObject jsonObject = readJson();

        jsonObject.getJSONArray("twitchKey").getJSONObject(0).put("OAuth2", token);
        try(FileWriter fileWriter = new FileWriter(FILE_NAME)) {
            fileWriter.write(jsonObject.toString(4));


        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to write token to source.json");
        }
    }

    //Получить токен телеграм бота
    public static String getTelegramBotToken() {
        return getValueTelegramKey("botToken");
    }


}
