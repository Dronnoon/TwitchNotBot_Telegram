package DataBase.Helper;

import DataBase.Entity.User;
import DataBase.Hibernate.HibernateUtil;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class UserHelper {
    private static SessionFactory sessionFactory;

    public UserHelper() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    public List<User> getUserByOptionalFieldList(Optional<String> fieldname, Optional<Object> value) {
        //Открываем сессию для манипуляции с персист объектами
        Session session = sessionFactory.openSession();

        //объект конструктор запросов для Criteria API
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        cq.select(root);

        if (fieldname.isPresent() && value.isPresent()) {
            String field = fieldname.get();

            // Проверяем, существует ли поле в классе User
            boolean fieldExists = false;
            try {
                User.class.getDeclaredField(field);
                fieldExists = true;
            } catch (NoSuchFieldException e) {
                System.err.println("Поле '" + field + "' не найдено в классе User");
            }

            // Если поле существует — строим фильтр
            if (fieldExists) {
                cq.where(cb.equal(root.get(field), value.get()));
            }
        }
        Query q = session.createQuery(cq);
        List<User> list = q.getResultList();
        session.close();

        return list;
    }

    public void setUserToDB(Long tg_chat_id, String user_name) {
        Session session = sessionFactory.openSession();

        //Формируем запрос
        User user = new User();
        user.setId(tg_chat_id);
        user.setUserName(user_name);

        //Открываем транзакцию
        session.getTransaction().begin();

        //Сохранение и отправка запроса
        session.persist(user);
        session.getTransaction().commit();

        //Закрытие сессии
        session.close();
    }

    public User getUserByID(Long tg_chat_id) {
        //Получаем пользователя по Id
        try (Session session = sessionFactory.openSession()) {
            return session.get(User.class, tg_chat_id);
        }
    }
}
