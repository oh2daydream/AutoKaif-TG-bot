import bot.Bot;
import bot.TelegramBotRunner;
import com.fasterxml.jackson.annotation.JsonFormat;
import config.DatabaseConfig;
import config.HibernateUtil;
import handler.BotCommandHandler;
import org.hibernate.SessionFactory;
import repository.CarRepository;
import repository.PostgresCarRepository;
import repository.PostgresRefuelingRepository;
import repository.RefuelingRepository;
import service.CarService;
import service.RefuelingService;
import javax.sql.DataSource;

public class Main {
    public static void main(String[] args) {
        // 1. Инициализируем единый пул соединений HikariCP
//      DataSource dataSource = DatabaseConfig.getDataSource();
        String dbUrl = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/autokaif");
        String dbUser = System.getenv().getOrDefault("DB_USER", "postgres");
        String dbPassword = System.getenv().getOrDefault("DB_PASSWORD", "postgres");
        SessionFactory sessionFactory = new HibernateUtil().buildSessionFactory(dbUrl,dbUser,dbPassword);
        // 2. Создаем SQL-репозитории вместо коллекций в памяти
        CarRepository carRepository = new PostgresCarRepository(sessionFactory);
        RefuelingRepository refuelingRepository = new PostgresRefuelingRepository(sessionFactory);

        // 3. Собираем остальные слои без изменений (DI)
        CarService carService = new CarService(carRepository);
        RefuelingService refuelingService = new RefuelingService(refuelingRepository);

        BotCommandHandler botHandler = new BotCommandHandler(
                refuelingService,
                carService
        );

        Bot bot = new TelegramBotRunner(botHandler);

        // 4. Запускаем бота
        bot.run();
    }
}