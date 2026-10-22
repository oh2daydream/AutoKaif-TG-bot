import bot.Bot;
import bot.TelegramBot;
import bot.TelegramBotRunner;
import handler.BotCommandHandler;
import handler.CarAddHandler;
import handler.RefuelingAddHandler;
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

        RefuelingAddHandler refuelingAddHandler = new RefuelingAddHandler(refuelingService, carService);
        CarAddHandler carAddHandler = new CarAddHandler(carService);

        BotCommandHandler botHandler = new BotCommandHandler(
                refuelingService,
                carService,
                refuelingAddHandler,
                carAddHandler
        );

        Bot bot = new TelegramBotRunner(botHandler);

        bot.run();
    }
}