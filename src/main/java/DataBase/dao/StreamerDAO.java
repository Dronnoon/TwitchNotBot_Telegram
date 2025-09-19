package DataBase.dao;

import DataBase.Entity.Streamer;
import DataBase.Helper.StreamerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StreamerDAO {
    private static StreamerHelper streamerHelper = new StreamerHelper();
    //Метод для добавления стримера
    public static void setStreamer(String twitch_id, String url, String username) {
        streamerHelper.setStreamerToDB(twitch_id, url, username);
    }

    //Получение списка всех стримеров
    public static List<String> getStreamers() {
        List<String> streamerList = new ArrayList<>();      //Создаём список для дальнейшего заполнения

        for (Streamer streamer : streamerHelper.getStreamerByOptionalFieldList(null, null)) {
            streamerList.add(streamer.getUsername());
        }
        return streamerList;        //Возвращаем список всех стримеров
    }

    //Метод для получения ссылки на стримера
    public static String getUrlStreamer(String username) {
        String url = null;

        for (Streamer streamer : streamerHelper.getStreamerByOptionalFieldList(Optional.of("username"), Optional.ofNullable(username))) {
            url = streamer.getUrl();
        }
        return url;     //Возвращаем ссылку на стримера
    }
}
