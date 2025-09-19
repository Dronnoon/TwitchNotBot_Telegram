package DataBase.Helper;

import DataBase.Entity.Statusnotification;
import DataBase.Hibernate.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class StatusnotificationHelper {

    private static SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public Statusnotification getStatusNotification(Long chatId) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Statusnotification.class, chatId);
        }
    }

    public void setStatusNotification(Long chatId, Boolean status) {
        Session session = sessionFactory.openSession();

        //Формирование отправляемого объекта
        Statusnotification statusnotification = new Statusnotification();
        statusnotification.setId(chatId);
        statusnotification.setStatus(status);

        //Открытие транзакции
        session.getTransaction().begin();

        //Сохранение значений в базу данных
        session.merge(statusnotification);
        session.getTransaction().commit();

        session.close();
    }
}
