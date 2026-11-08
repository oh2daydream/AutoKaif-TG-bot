package config;


import model.Car;
import model.Refueling;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    public static SessionFactory buildSessionFactory(String dbUrl, String dbUser, String dbPassword){
        Configuration configuration = new Configuration();

        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.connection.url", dbUrl);
        configuration.setProperty("hibernate.connection.username", dbUser);
        configuration.setProperty("hibernate.connection.password", dbPassword);

        // Полезно для отладки: Hibernate будет красиво печатать сгенерированный SQL в консоль
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.format_sql", "true");

        // Регистрируем наши классы-сущности
        configuration.addAnnotatedClass(Car.class);
        configuration.addAnnotatedClass(Refueling.class);

        return configuration.buildSessionFactory();
    }
}
