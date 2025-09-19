package DataBase.Helper;


import DataBase.Entity.Streamer;
import DataBase.Hibernate.HibernateUtil;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class StreamerHelper {
    private static SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public StreamerHelper() {
    }

    public List<Streamer> getStreamerByOptionalFieldList(Optional<String> fieldname, Optional<Object> value) {
        //Открываем сессию для манипуляции с персист объектами
        Session session = sessionFactory.openSession();

        //объект конструктор запросов для Criteria API
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Streamer> cq = cb.createQuery(Streamer.class);
        Root<Streamer> root = cq.from(Streamer.class);

        //Если есть значение, то выполнится выборка
        if (value != null) {
            cq.where(cb.equal(root.get(String.valueOf(fieldname)), value));
        }

        Query q = session.createQuery(cq);
        List<Streamer> list = q.getResultList();
        session.close();

        return list;
    }

    public void setStreamerToDB(String twitch_id, String url, String username) {
        Session session = sessionFactory.openSession();

        //Формируем запрос
        Streamer streamer = new Streamer();
        streamer.setTwitchId(twitch_id);
        streamer.setUrl(url);
        streamer.setUsername(username);

        //Открываем транзакцию
        session.getTransaction().begin();

        //Сохранение и отправка запроса
        session.persist(streamer);
        session.getTransaction().commit();

        //Закрытие сессии
        session.close();
    }

    public Streamer getStreamerById(String twitch_id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Streamer.class, twitch_id);
        }
    }

}
