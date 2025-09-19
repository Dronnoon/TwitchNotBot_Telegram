package telegram;

//import DataBase.DB;

import DataBase.Helper.StatusnotificationHelper;
import InteractionUserAndStreamer.AddAndDeleteFlagList;
import UrlChecked.UrlValidate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Deque;
import java.util.List;

import static DataBase.dao.StreamerDAO.getUrlStreamer;
import static DataBase.dao.StreamerToUsersDAO.deleteStreamerAtUser;
import static DataBase.dao.StreamerToUsersDAO.getStreamerToUsers;
import static DataBase.dao.UsersDAO.setUser;
import static DataBase.dao.UsersDAO.userMathInDb;
import static InteractionUserAndStreamer.AddUserToStreamerFlag.addUserToStreamer;
import static InteractionUserAndStreamer.DeleteUserToStreamerFlag.deleteUserToStreamer;
import static JSON.JsonSource.getTelegramBotToken;
import static telegram.BotCollector.trackMessage;
import static telegram.ShowStreamer.tgSendStreamer;
import static telegram.TgKeyboard.*;
import static twitch.TwitchApi.getTwitchId;

public class TgBot extends TelegramLongPollingBot implements Runnable {

    //Данный метод необходим для запуска бота в отдельном потоке
    @Override
    public void run() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new TgBot());
            System.out.println("Бот успешно запущен!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "twitchNot_bot";
    }   //Прописываем имя бота

    @Override
    public String getBotToken() {
        return getTelegramBotToken();
    }   //Получаем токен


    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {

            Long chatId = update.getMessage().getChatId();      //Получаем chatId из сообщения
            String text = update.getMessage().getText();        //Получаем текст из сообщения
            String tgName = update.getMessage().getChat().getUserName();        //Получаем username из сообщения

            //Текст сообщения передаётся в метод, где проверяется, является ли он ссылкой
            if (UrlValidate.validateUrl(text)) {

                AddAndDeleteFlagList checkFlag = AddAndDeleteFlagList.getInstance();
                //Выполняем удаление пользователя, если его флаг стоит в списке
                if (checkFlag.checkStateDeleteList(chatId)) {
                    deleteUserToStreamer(chatId, text);

                //Выполняем добавление пользователя, если его флаг имеется в списке
                } else if (checkFlag.checkStateAddList(chatId)) {
                    addUserToStreamer(chatId, text);

                //Если ссылка не проходит валидацию, пользователю выводит это сообщение
                } else {
                    sendMessageOnTg(chatId, "Я не могу понять, что вы хотите сделать со ссылкой. Пожалуйста, начните процесс заново.");
                }
            } else {
                switch (text) {
                    // При срабатывании кнопки, проверяем пользователя на наличие в базах
                    case "/start":
                        //Если пользователь не найден, то добавляем его в базу
                        if (!userMathInDb(chatId)) {
                            ShowSettings settings = new ShowSettings();

                            setUser(chatId, tgName);
                            settings.turnOnStatusNotification(chatId);
                        }
                        technicalSendMessageOnTg(chatId, "Добро пожаловать! С помощью этого бота, вы можете отслеживать каналы twitch-стримеров в одном месте!\n Используя клавиши ниже, вы можете добавить, убрать, посмотреть список ваших стримеров." +
                                        " По умолчанию включены уведомления об окончании трансляций. Отключить вы их можете по нажатию кнопки \"Настройки\".",
                                replyKeyboardMarkup());
                        break;
                    //При срабатывании кнопки добавить, пользователя переводит в метод, где тот взаимодействует с ботом для добавления стримера
                    case "✅ Добавить":
                        addUserToStreamer(chatId, text);
                        break;
                    //При срабатывании запускает метод, взаимодействуя с которым пользователь удалит стримера из списка
                    case "❌ Убрать":
                        deleteUserToStreamer(chatId, text);
                        break;
                    //При срабатывании переводит пользователя в метод, где по итогу ему выдаст весь список с его стримерами
                    case "\uD83D\uDCDD Просмотреть":
                        sendMessageOnTg(chatId, "Вот список стримеров: ");
                        tgSendStreamer(chatId);
                        break;
                    case "⚙\uFE0F Настройки":
                        sendMessageOnTg(chatId, "Настройки:\n" +
                                        "• Уведомления об окончании трансляции.",
                                inlineKeyboardSettings(chatId));
                        break;
                    //По умолчанию, если введённое пользователем сообщение не пройдёт не в один кейс, то выдаст сообщение
                    default:
                        sendMessageOnTg(chatId, "Не могу считать команду. Пожалуйста, попробуйте ещё раз.");
                }
            }
            //Если ответ пользователя будет содержать ответ от InlineKeyboard, будет обрабатываться это условие
        } else if (update.hasCallbackQuery()) {

            //Инициализируем переменные, и присваиваем в них chatId и ответ от InlineKeyboard кнопки
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            String callbackQuery = update.getCallbackQuery().getData();

            //В данной условной конструкции обрабатываются значения из ответа
            //Если начинается на "deleteStreamerAtUsers_", мы берём из ответа всё что после знака "_"
            if (callbackQuery.startsWith("deleteStreamerAtUsers_")) {
                String streamerToDelete = callbackQuery.substring("deleteStreamerAtUsers_".length());
                deleteStreamerAtUser(getTwitchId(streamerToDelete), update.getCallbackQuery().getFrom().getId());    //Запускает метод, который удалит стримера
                technicalSendMessageOnTg(chatId, "Вы удалили " + streamerToDelete + " из списка отслеживаемых");    //Выводит нестираемое сообщение об удалении в чат
                deleteLastMessage(chatId);  //Чистим чат от мусорных сообщений

                // Убираем список, который выводит пользователь
            } else if (callbackQuery.equals("hideList")) {
                deleteLastMessage(chatId);
            } else if (callbackQuery.startsWith("turnOffEndNotification_")) {
                //Вызываем метод, который отключит уведомления
                ShowSettings settings = new ShowSettings();

                settings.turnOffStatusNotification(chatId);

            } else if (callbackQuery.startsWith("turnOnEndNotification_")) {
                //Вызываем метод, который включит уведомления
                ShowSettings settings = new ShowSettings();

                settings.turnOnStatusNotification(chatId);
            } else {
                sendMessageOnTg(chatId, "Я не смог считать команду. Пожалуйста попробуйте ещё раз.");
            }
        }
    }

    /* Существует 2 типа сообщений: sendMessageOnTg - самые обычные, они могут удаляться
    Второй тип это technicalSendMessageOnTg - они нужны для отправки сообщений по типу "Стример добавлен".
    Они не могут быть удалены */

    //Этот метод отправляет только переданный в него текст. Оно будет удалено позже
    public void sendMessageOnTg(Long chatId, String text) {
        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .build();      //Формируем сообщение
        try {
            trackMessage(chatId, execute(message));     //Добавляем это сообщение в BotCollector
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //Отправляет стираемое сообщение с нижней клавиатурой
    public void sendMessageOnTg(Long chatId, String text, ReplyKeyboardMarkup keyboard) {
        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(keyboard)
                .build();
        try {
            trackMessage(chatId, execute(message));     //Отслежваем сообщение в BotCollector

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //Создаёт стираемое сообщение с клавиатурой у самого сообщения
    public void sendMessageOnTg(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(keyboard)      //Добавляем клавиатуру в сообщение
                .build();
        try {
            trackMessage(chatId, execute(message));     //Отслеживаем сообщение

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //Этот метод отправляет сообщение, которое содержит только текст. Оно не может быть удалено
    public void technicalSendMessageOnTg(Long chatId, String text) {
        SendMessage message = SendMessage
                .builder().
                chatId(chatId).     //Указываем chatId
                text(text).     //Добавляем текст
                build();      //Формируем сообщение
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //Формируем сообщение, которое помимо текста вызывает нижнюю клавиатуру
    public void technicalSendMessageOnTg(Long chatId, String text, ReplyKeyboardMarkup keyboard) {
        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(keyboard)      //Добавляем клавиатуру
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //Данный метод отправляет оповещение о начале трансляции всем подписанным на стримера пользователям
    public void sendNotificationMessage(String streamerName, String text) {
        List<Long> users = getStreamerToUsers(getTwitchId(streamerName));        //Получаем список всех пользователей конкретного стримера
        StatusnotificationHelper notificationHelper = new StatusnotificationHelper();

        //Методом перебора, отправляем сообщение, содержащие имя стримера, текст события, и url адрес трансляции
        for (Long user : users) {
            if (notificationHelper.getStatusNotification(user).getStatus()) {
                technicalSendMessageOnTg(user, streamerName + " " + text + "\n" + getUrlStreamer(streamerName));
            }
        }
    }

    //Данный метод удаляет последние сообщения, добавленные в BotCollector
    public void deleteLastMessage(Long chatId) {
        BotCollector messageManager = new BotCollector();
        Deque<Integer> tracked = messageManager.getTrackedMessages(chatId);     //Получаем все id сообщений

        //В конструкции перебором удаляем каждое сообщение из списка
        try {
            for (Integer delete : tracked) {
                execute(new DeleteMessage(chatId.toString(), delete));      //Отправляем команду на удаление, передавая chatId и messageId
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Исключение, если телеграм не выполнит команду в блоке try
        }
        messageManager.clear(chatId);       //выполняем очистку BotCollector от сообщений этого пользователя
    }

}