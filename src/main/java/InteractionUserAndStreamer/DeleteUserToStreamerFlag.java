package InteractionUserAndStreamer;

import DataBase.dao.StreamerDAO;
import DataBase.dao.StreamerToUsersDAO;
import telegram.TgBot;

import static DataBase.dao.StreamerToUsersDAO.deleteStreamerAtUser;
import static UrlChecked.UrlValidate.extractStreamerId;
import static UrlChecked.UrlValidate.validateUrl;
import static twitch.TwitchApi.getTwitchId;

public class DeleteUserToStreamerFlag {
    static TgBot tgBot = new TgBot();
    static AddAndDeleteFlagList flag = AddAndDeleteFlagList.getInstance();

    //Данный метод нужен для удаления соотношения пользователь-стример
    public static void deleteUserToStreamer(Long chatId, String text) {
        if (text.equals("❌ Убрать")) {      //Если человек нажал убрать на нижней клавиатуре
            flag.clearWaitListAdd(chatId);       //Убираем человека из флаг-листа на добавление
            flag.addUserToDeleteList(chatId);        //Добавляем пользователя во флаг-лист на удаление
            tgBot.sendMessageOnTg(chatId, "Введите ссылку на стримера: ");      //Просим прислать ссылку на стримера

        } else if (flag.checkStateDeleteList(chatId)) {     //Если человек находится в списке на удаление
            String streamerName = extractStreamerId(text);      //Получаем имя стримера из ссылки
            if (validateUrl(text) && streamerName != null) {        //Если ссылка является ссылкой и имя стримера не null

                //Проверяем, есть ли соотношение, и тогда удаляем
                if (StreamerToUsersDAO.getUserToStreamers(chatId).contains(getTwitchId(streamerName))) {
                    deleteStreamerAtUser(getTwitchId(streamerName), chatId);     //Удаляем соотношение пользователь стример
                    flag.clearWaitListDelete(chatId);    // Сбрасываем флаг

                    tgBot.technicalSendMessageOnTg(chatId, "Стример " + streamerName + " был удалён!\n" +
                            "Можете посмотреть список отслеживаемых стримеров по нажатию кнопки \"Просмотреть\".");     //Отправляем сообщение об удалении
                    tgBot.deleteLastMessage(chatId);        //Удаляем ненужные сообщения
                } else {
                    tgBot.sendMessageOnTg(chatId, "Стример " + streamerName + " отсутствует в вашем списке. Можете посмотреть список отслеживаемых стримеров по нажатию кнопки \"Просмотреть\".");
                }

            } else {
                tgBot.sendMessageOnTg(chatId, "Это не похоже на ссылку. Попробуйте еще раз.");      //Сообщение, если ссылка не распознана
            }
        } else {
            tgBot.sendMessageOnTg(chatId, "Я вас не понял. Используйте кнопки.");       //Сообщение если действие не понятно
        }
    }
}
