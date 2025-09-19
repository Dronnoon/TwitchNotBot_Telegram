package telegram;

import DataBase.Entity.Statusnotification;
import DataBase.Helper.StatusnotificationHelper;

public class ShowSettings {
    TgBot tgBot = new TgBot();
    StatusnotificationHelper helper = new StatusnotificationHelper();

    public void turnOnStatusNotification(Long chatId) {
        if (helper.getStatusNotification(chatId) == null) {
            helper.setStatusNotification(chatId, true);

        } else if (!helper.getStatusNotification(chatId).getStatus()) {
            helper.setStatusNotification(chatId, true);
            tgBot.sendMessageOnTg(chatId, "Вы включили уведомления об окончании трансляций.");
        }
    }

    public void turnOffStatusNotification(Long chatId) {
        if (helper.getStatusNotification(chatId).getStatus()) {
            helper.setStatusNotification(chatId, false);

            tgBot.sendMessageOnTg(chatId, "Вы отключили уведомления об окончании трансляций.");
        }
    }
}
