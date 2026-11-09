import bot.Bot;
import bot.TelegramBotRunner;
import config.HibernateUtil;
import handler.BotCommandHandler;
import org.hibernate.SessionFactory;
import repository.CarRepository;
import repository.PostgresCarRepository;
import repository.PostgresRefuelingRepository;
import repository.RefuelingRepository;
import service.CarService;
import service.RefuelingService;

public class Main {
    public static void main(String[] args) {
        String dbUrl = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5433/car_logbook?sslmode=disable");
        String dbUser = System.getenv().getOrDefault("DB_USER", "car_user");
        String dbPassword = System.getenv().getOrDefault("DB_PASSWORD", "ваш_пароль");

        SessionFactory sessionFactory = HibernateUtil.buildSessionFactory(dbUrl, dbUser, dbPassword);


        CarRepository carRepository = new PostgresCarRepository(sessionFactory);
        RefuelingRepository refuelingRepository = new PostgresRefuelingRepository(sessionFactory);


        CarService carService = new CarService(carRepository);
        RefuelingService refuelingService = new RefuelingService(refuelingRepository);

        BotCommandHandler botHandler = new BotCommandHandler(
                refuelingService,
                carService
        );


        Bot bot = new TelegramBotRunner(botHandler);
        bot.run();
    }
}