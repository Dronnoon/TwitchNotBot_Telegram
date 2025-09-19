package telegram;

import DataBase.Helper.StatusnotificationHelper;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class TgKeyboard {

    //Эта клавиатура появляется у пользователя внизу приложения
    public static ReplyKeyboardMarkup replyKeyboardMarkup() {

        //Возвращаем builder с кнопками
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(          //Создаём лист, в который мы положим 2 ряда(листов) кнопок
                        new KeyboardRow(List.of(        //Первый ряд(лист) с кнопками
                                KeyboardButton.builder().text("✅ Добавить").build(),    //Создаём кнопку "Добавить", отсылающую аналогичный текст
                                KeyboardButton.builder().text("❌ Убрать").build()       //Создаём кнопку "Убрать", отсылающую аналогичный текст
                        )),
                        new KeyboardRow(List.of(        //Второй ряд(лист) с кнопками
                                KeyboardButton.builder().text("\uD83D\uDCDD Просмотреть").build(),       //Создаёт кнопку "Просмотреть", отсылающую аналогичный текст
                                KeyboardButton.builder().text("⚙\uFE0F Настройки").build()
                        ))
                ))
                .resizeKeyboard(true)       //Подгоняет размер клавиатуры
                .oneTimeKeyboard(false)     //Клавиатура всегда будет в чате внизу
                .build();
    }

    //Данная клавиатура появляется под конкретным сообщением с которым вызвана, и при  нажатии отправляет свои метаданные
    public static InlineKeyboardMarkup inlineKeyboardDeleteStreamer(String streamer) {
        //Возвращаем builder, где будет структура клавиатуры
        return InlineKeyboardMarkup.builder()
                .keyboardRow(
                        /*Создаём лист, в который положим только одну кнопку "Удалить",
                         которая будет отправлять метаданные с припиской в виде имени стримера в конце.
                         Имя будет ключом к тому, какого стримера хотят удалить*/
                        List.of(
                                InlineKeyboardButton.builder().text("❌ Удалить").callbackData("deleteStreamerAtUsers_" + streamer).build()))
                .build();
    }

    //Данная клавиатура появляется под конкретным сообщением с которым вызвана, и при  нажатии отправляет свои метаданные
    public static InlineKeyboardMarkup inlineKeyboardSettings(Long chatId) {
        StatusnotificationHelper helper = new StatusnotificationHelper();
        //Возвращаем builder, где будет структура клавиатуры
        return InlineKeyboardMarkup.builder()
                .keyboardRow(
                        /*Создаём лист, в который положим только одну кнопку "Удалить",
                         которая будет отправлять метаданные с припиской в виде имени стримера в конце.
                         Имя будет ключом к тому, какого стримера хотят удалить*/
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text(helper.getStatusNotification(chatId).getStatus() ? "Выключить \uD83D\uDD14" : "Включить \uD83D\uDD14")
                                        .callbackData(helper.getStatusNotification(chatId).getStatus() ? "turnOffEndNotification_" : "turnOnEndNotification_")
                                        .build()))
                .build();
    }

    //Данная клавиатура нужна для удаления сообщений со списком стримеров
    public static InlineKeyboardMarkup keyboardHideList() {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(
                        //Создаём лист в который положим только кнопку "Убрать список"
                        List.of(
                                InlineKeyboardButton.builder().text("☑\uFE0F Убрать список").callbackData("hideList").build()))
                .build();
    }
}
