package DataBase.Helper;

import DataBase.Entity.Streamertouser;
import DataBase.Hibernate.HibernateUtil;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class StreamertouserHelper {
    private static SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public StreamertouserHelper() {
    }

    public List<Streamertouser> getListByField(String fieldname, Object value) {
        //Открываем сессию для манипуляции с персист объектами
        Session session = sessionFactory.openSession();

        //объект конструктор запросов для Criteria API
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Streamertouser> cq = cb.createQuery(Streamertouser.class);
        Root<Streamertouser> root = cq.from(Streamertouser.class);  //Первостепенный, корневой entity (в sql запросе from)
        cq.where(cb.equal(root.get(fieldname), value)); //необязательный оператор, если нужно получить все значения

        Query q = session.createQuery(cq);
        List<Streamertouser> list = q.getResultList();
        session.close();
        return list;
    }

    public void setStreamertouser(Long user_id, String streamer_id) {
        Session session = sessionFactory.openSession();

        //Формируем запрос
        Streamertouser streamertouser = new Streamertouser();
        streamertouser.setUserId(user_id);
        streamertouser.setStreamerId(streamer_id);

        //Открываем транзакцию
        session.getTransaction().begin();

        //Сохранение и отправка запроса
        session.persist(streamertouser);
        session.getTransaction().commit();

        //Закрытие сессии
        session.close();
    }

    public void deleteStreamertouser(Long user_id, String streamer_id) {
        Session session = sessionFactory.openSession();

        //Формируем запрос
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaDelete<Streamertouser> cd = cb.createCriteriaDelete(Streamertouser.class);
        Root<Streamertouser> root = cd.from(Streamertouser.class);


        //Переменные принимают фрагменты кода, которые создают эквивалент "имя = имя"
        Predicate predicateUser = cb.equal(root.get("userId"), user_id);
        Predicate predicateStreamer = cb.equal(root.get("streamerId"), streamer_id);

        //Формируем запрос, чтобы была выбрана только та строка, которая имеет в себе и определённое имя и определённого стримера
        cd.where(cb.and(predicateUser, predicateStreamer));

        //Открываем транзакцию, отправляем запрос, закрываем транзакцию и сессию
        session.getTransaction().begin();
        session.createQuery(cd).executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

}
