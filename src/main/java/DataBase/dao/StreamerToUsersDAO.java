package DataBase.dao;

import DataBase.Entity.Streamertouser;
import DataBase.Helper.StreamertouserHelper;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamerToUsersDAO {
    private static final StreamertouserHelper streamertouserHelper = new StreamertouserHelper();

    //Получение списка отслеживаемых стримеров у пользователя
    public static List<String> getUserToStreamers(Long tgChatId) {
        List<String> streamerList = new ArrayList<>();      //Создаём список для дальнейшего заполнения

        for (Streamertouser streamertouser : new StreamertouserHelper().getListByField("userId", tgChatId)) {
            streamerList.add(streamertouser.getStreamerId());
            System.out.println(streamertouser.getStreamerId());
        }

        return streamerList;    //Возвращаем список стримеров
    }

    //Добавление соотношения пользователь-стример, для добавления в пул отправки оповещений
    public static void setUserOnStreamer(String streamerId, Long userChatId) {

        streamertouserHelper.setStreamertouser(userChatId, streamerId);
    }

    //Удаление соотношения стример-пользователь, чтобы не отправлялось уведомление
    public static void deleteStreamerAtUser(String streamerId, Long userChatId) {
        streamertouserHelper.deleteStreamertouser(userChatId, streamerId);
    }

    //Получение списка пользователей у конкретного стримера
    public static List<Long> getStreamerToUsers(String streamerId) {
        List<Long> userList = new ArrayList<>();        //Создаём список для дальнейшего заполнения и отправки

        for (Streamertouser streamertouser : new StreamertouserHelper().getListByField("streamerId", streamerId)) {
            userList.add(streamertouser.getUserId());
        }

        return userList;         //Возвращаем список с пользователями
    }
}

