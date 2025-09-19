package twitch;

import JSON.JsonSource;
import org.json.JSONObject;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ValidateOAuth {

    //Данный метод нужен для проверки OAuth токена
    private String validateOAuth() {

        //Получаем актуальный токен
        String oAuth = JsonSource.getOAuth2Token();

        //В данной конструкции мы проверяем токен
        try {

            /* В этом блоке кода, создаём экземпляр класса ProcessBuilder(нужен для отправки сообщения в cmd),
             и отсылаем команду с проверяемым токеном */

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://id.twitch.tv/oauth2/validate"))
                    .header("Authorization", "OAuth " + oAuth)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            //В данном блоке кода, мы получаем ответ от командной строки, и записываем его в переменную line
            String line = response.body();

            //В данной условной конструкции проверяем, валидируется токен, или нет
            JSONObject json = new JSONObject(line);
            if(json.has("status") && json.getInt("status") == 401) {
                System.out.println("Token isn't valid" + "\nHttp status: " + response.statusCode() );
                oAuth = setToken();
            } else{
                System.out.println("Token is valid");
            }

            //Возвращаем токен
            return oAuth;
        } catch (IOException | InterruptedException e) {    //Если не получилось проверить, то возвращаем null
            e.printStackTrace();
            return null;
        }
    }

    private String setToken() {
        //получение токена
        String oAuth;
        String clientId = JsonSource.getClientId();
        String clientSecret = JsonSource.getClientSecret();

        try {
            //Формируем http запрос для получение токена, передавая: clientID, clientSecret
            String body = "client_id=" + clientId
                    + "&client_secret=" + clientSecret
                    + "&grant_type=client_credentials";

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://id.twitch.tv/oauth2/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            //Проверяем, какой ответ получен от твича
            if(response.statusCode() != 200) {
                throw new RuntimeException("Ошибка при получении токена" + response.statusCode());
            }
             else {
                System.out.println("Токен успешно получен: " + response.statusCode());
            }

            JSONObject json = new JSONObject(response.body());

            oAuth = json.getString("access_token");

            //Записываем токен в source.json
            JsonSource.setOAuthtoken(oAuth);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Не удалось получить токен" + e.toString());
        }
        return oAuth;
    }

    //Данный метод возвращает значение OAuth токена
    public String getOAuthToken() {
        return validateOAuth();
    }
}
