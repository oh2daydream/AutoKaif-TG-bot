import bot.Bot;
import bot.TelegramBotRunner;
import config.DatabaseConfig;
import handler.BotCommandHandler;
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
        DataSource dataSource = DatabaseConfig.getDataSource();

        // 2. Создаем SQL-репозитории вместо коллекций в памяти
        CarRepository carRepository = new PostgresCarRepository(dataSource);
        RefuelingRepository refuelingRepository = new PostgresRefuelingRepository(dataSource);

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