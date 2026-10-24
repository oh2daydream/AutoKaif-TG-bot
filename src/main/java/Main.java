import bot.Bot;
import bot.TelegramBotRunner;
import handler.BotCommandHandler;
import repository.CarRepository;
import repository.InMemoryCarRepository;
import repository.InMemoryRefuelingRepository;
import repository.RefuelingRepository;
import service.CarService;
import service.RefuelingService;


public class Main {
    public static void main(String[] args) {
        CarRepository carRepository = new InMemoryCarRepository();
        RefuelingRepository refuelingRepository = new InMemoryRefuelingRepository();

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