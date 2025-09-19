package telegram;

import DataBase.Helper.StreamerHelper;

import java.util.List;

import static DataBase.dao.StreamerToUsersDAO.getUserToStreamers;
import static telegram.TgKeyboard.inlineKeyboardDeleteStreamer;
import static telegram.TgKeyboard.keyboardHideList;

public class ShowStreamer {

    //В данном методе, мы отправляем всех стримеров по одному в сообщении
    public static void tgSendStreamer(Long tgChatId) {
        TgBot tgBot = new TgBot();  //Создаём экземпляр класса TgBot
        StreamerHelper streamerHelper = new StreamerHelper();
        List<String> streamersList;     //Создаём список для дальнейшего заполнения
        try {
            streamersList = getUserToStreamers(tgChatId);       //В список записываем всех стримеров, полученных из метода getUserToStreamers()
        } catch (Exception e) {
            throw new RuntimeException(e);      //Исключение, если блок try не выполнится
        }

        //С помощью перебора, мы отправляем сообщения с именами стримера в телеграм чат
        for (String streamerId : streamersList) {
            /*В sendMessageOnTg мы передаём id чата, имя стримера, и в inlineKeyboardDeleteStreamer передаём имя стримера,
             чтобы в дальнейшем по метаданным можно было идентифицировать конкретное сообщение*/
            String streamerName = streamerHelper.getStreamerById(streamerId).getUsername();
            tgBot.sendMessageOnTg(tgChatId, streamerName, inlineKeyboardDeleteStreamer(streamerName));
        }

        //Отправляем сообщение о том, что нажатием на кнопку, можно убрать выведенный список
        tgBot.sendMessageOnTg(tgChatId, "Нажмите, чтобы убрать список.", keyboardHideList());
    }
}
