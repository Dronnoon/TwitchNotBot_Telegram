package twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.helix.domain.UserList;
import telegram.TgBot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static DataBase.dao.StreamerDAO.getStreamers;


public class TwitchApi {

    //Переменная принимает значение oAuth токена из метода
    static ValidateOAuth validAuth = new ValidateOAuth();

    private static String oAuth2() {
        return validAuth.getOAuthToken();
    }

    //Создаём токен по умолчанию в формате OAuth2Credential, передавая в него наш токен
    private static OAuth2Credential defOAuthToken2 = new OAuth2Credential("twitch", oAuth2());

    //При помощи builder создаём экземпляр класса TwichClient с необходимыми нам модулями
    static TwitchClient twitchClient = TwitchClientBuilder.builder()
            .withEnableHelix(true) // Включаем доступ к API Twitch
            .withDefaultAuthToken(defOAuthToken2)
            .build();

    //Создаём переменную channelNames, в которую будут передаваться все каналы, которые должны отправлять уведомления
    private List<String> channelNames;

    //Этот метод будет добавлять новые каналы в наш List список в течении 5 минут
    public void setChannels(List<String> channelNames) {
        this.channelNames = channelNames;
    }

    //С помощью этого метода мы получаем каналы из базы данных
    public List<String> getChannels() {
        return channelNames = getStreamers();
    }

    public static void main(String[] args) {

        //Инициализируем экземпляры классов TgBot и Thread, и запускаем в отдельном потоке нашего телеграм бота
        TgBot start = new TgBot();
        Thread thread = new Thread(start);
        thread.start();

        //Создаём экземпляр класса TwitchApi и ScheduledExecutorService
        TwitchApi twitchApi = new TwitchApi();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        //При помощи лямбда-выражения мы синхронизируем с базой данных список стримеров раз в 5 минут, и загружаем в метод, где он отслеживает  их
        scheduler.scheduleAtFixedRate(() -> {
            twitchApi.syncChannelListeners(getStreamers());
            for (String streamer : getStreamers()) {
                System.out.println(streamer);
            }
            twitchClient.getClientHelper().enableStreamEventListener(twitchApi.getChannels());
        }, 0, 5, TimeUnit.MINUTES); // раз в 5 минут


        /* В данном блоке кода, мы запускаем срабатывание на событие ChannelGoLiveEvent, которое срабатывает,
        когда стример запускает трансляцию. В таком случае, мы в бота отправляем сообщение, где фигурирует имя стримера и текст события */
        twitchClient.getEventManager().onEvent(ChannelGoLiveEvent.class, event -> {
            System.out.println(event.getChannel().getName() + " начал прямую трансляцию!");
            TgBot tgBot = new TgBot();

            tgBot.sendNotificationMessage(event.getChannel().getName(), "начал прямую трансляцию!");

        });

       /* В данном блоке кода, мы запускаем срабатывание на событие ChannelGoOfflineEvent, которое срабатывает,
        когда стример завершает трансляцию. В таком случае, мы в бота отправляем сообщение, где фигурирует имя стримера и текст события */
        twitchClient.getEventManager().onEvent(ChannelGoOfflineEvent.class, event -> {
            System.out.println(event.getChannel().getName() + " закончил прямую трансляцию!");
            TgBot tgBot = new TgBot();

            tgBot.sendNotificationMessage(event.getChannel().getName(), "закончил прямую трансляцию!");
        });

//        twitchClient.getChat().joinChannel(getStreamers().get(2));

//        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
//            System.out.println("[" + event.getChannel().getName() + "] " + event.userMathInDb().getName() + ": " + event.getMessage());
//
//        });
    }

    //Данный метод нужен для синхронизации стримеров из базы данных с теми, которые крутятся в коде
    public void syncChannelListeners(List<String> updatedChannels) {
        // Вычисляем добавленных стримеров
        List<String> toAdd = new ArrayList<>(updatedChannels);
        toAdd.removeAll(getChannels());

        // Вычисляем удалённых стримеров
        Set<String> toRemove = new HashSet<>(channelNames);
        toRemove.removeAll(updatedChannels);

        //Если список удалённых стримеров не пустой, то мы удаляем их из листа
        if (!toRemove.isEmpty()) {
            twitchClient.getClientHelper().disableStreamEventListener(new ArrayList<>(toRemove));
        }

        //Если список добавленных не пустой, то добавляем новых стримеров
        if (!toAdd.isEmpty()) {
            twitchClient.getClientHelper().enableStreamEventListener(new ArrayList<>(toAdd));
        }

        // Обновляем текущее состояние
        setChannels(updatedChannels);
    }


    //Данный метод нужен для валидации стримера по никнейму
    public static String getTwitchId(String channelName) {

        if (channelName == null) {
            return null;        //если имя канала пустое, то возвращается null
        }

        //Переменная типа UserList принимает все совпадения по имени стримера
        UserList streamer = twitchClient.getHelix().getUsers(null, null, List.of(channelName)).execute();

        //Инициализируем переменную для дальнейшего заполнения
        String channelId = null;

        //В данной условной конструкции мы проверяем возвратное значение: Если список не пустой, то возвращаем первый id, иначе возвращаем пустое значение
        if (!streamer.getUsers().isEmpty()) {
            channelId = streamer.getUsers().get(0).getId();
            return channelId;
        } else {
            return null;
        }
    }
}
