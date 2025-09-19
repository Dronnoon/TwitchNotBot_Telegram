package InteractionUserAndStreamer;

import DataBase.dao.StreamerDAO;
import telegram.TgBot;

import static DataBase.dao.StreamerDAO.getStreamers;
import static DataBase.dao.StreamerDAO.setStreamer;
import static DataBase.dao.StreamerToUsersDAO.getUserToStreamers;
import static DataBase.dao.StreamerToUsersDAO.setUserOnStreamer;
import static UrlChecked.UrlValidate.extractStreamerId;
import static UrlChecked.UrlValidate.validateUrl;
import static twitch.TwitchApi.getTwitchId;

public class AddUserToStreamerFlag {
    static TgBot tgBot = new TgBot();
    static AddAndDeleteFlagList flag = AddAndDeleteFlagList.getInstance();

    //Метод для добавления пользователя в пул стримера
    public static void addUserToStreamer(Long chatId, String text) {
        if (text.equals("✅ Добавить")) {        //Если человек нажал добавить на нижней клавиатуре
            flag.clearWaitListDelete(chatId);        //Очищаем(если есть) пользователя из флаг-листа на удаление
            flag.addUserToAddList(chatId);       //Добавляем пользователя во флаг-лист добавления
            tgBot.sendMessageOnTg(chatId, "Введите ссылку на стримера: ");      //Отсылаем сообщение с просьбой выслать ссылку
        } else if (flag.checkStateAddList(chatId)) {        //Если пользователь найден во флаг-листе на добавление
            String streamerName = extractStreamerId(text);      //Получаем имя стримера
            if (validateUrl(text) && streamerName != null) {        //Если ссылка является ссылкой и имя стримера не пустое
                System.out.println("Прошёл проверку");

                //Проверяем, есть ли стример в базе данных. Если нет, то добавляем в таблицу стримеров
                if (!getStreamers().contains(streamerName)) {
                    setStreamer(getTwitchId(streamerName), text, streamerName);
                }

                //Если соотношения нет, то добавляем его
                if (!getUserToStreamers(chatId).contains(getTwitchId(streamerName))) {
                    System.out.println("Соотношение не найдено");
                    try {
                        setUserOnStreamer(getTwitchId(streamerName), chatId);       //Добавляем соотношение пользователь-стример в базу данных
                        tgBot.technicalSendMessageOnTg(chatId, "Стример " + streamerName + " был добавлен в отслеживаемое!\n" +
                                "Можете посмотреть список отслеживаемых стримеров по нажатию кнопки \"Просмотреть\".");     //Отправляем сообщение о добавлении

                        tgBot.deleteLastMessage(chatId);        //Удаляем лишние сообщения от бота
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    tgBot.sendMessageOnTg(chatId, "Стример " + streamerName + " уже есть в вашем списке. Можете посмотреть список отслеживаемых стримеров по нажатию кнопки \"Просмотреть\".");
                }

                flag.clearWaitListAdd(chatId);  // Сбрасываем флаг


            } else {
                tgBot.sendMessageOnTg(chatId, "Это не похоже на ссылку. Попробуйте еще раз.");      //Если не получилось понять ссылку, то отсылаем это сообщение
            }
        } else {
            tgBot.sendMessageOnTg(chatId, "Я вас не понял. Используйте кнопки.");       //Если не получилось распознать команду
        }
    }
}
