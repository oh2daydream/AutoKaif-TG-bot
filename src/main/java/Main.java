import bot.TelegramBot;
import handler.BotCommandHandler;
import handler.CarAddHandler;
import handler.RefuelingAddHandler;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import repository.CarRepository;
import repository.InMemoryCarRepository;
import repository.InMemoryRefuelingRepository;
import repository.RefuelingRepository;
import service.CarService;
import service.RefuelingService;


public class Main {
    public static void main(String[] args) {
        String botTonken = System.getenv("BOT_TOKEN");
        String botUsername = System.getenv("BOT_USERNAME");
        if(botTonken == null || botTonken.isBlank() || botUsername == null || botUsername.isBlank()){
            System.err.println("❌ Ошибка: не задана переменная окружения BOT_TOKEN или BOT_USERNAME!");
            System.err.println("Укажите BOT_TOKEN и BOT_USERNAME в настройках запуска (Run/Debug Configurations -> Environment variables).");
            return;
        }
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

        try{
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            TelegramBot bot = new TelegramBot(botTonken, botUsername, botHandler);
            botsApi.registerBot(bot);

            System.out.println("==================================================");
            System.out.println("🚀 Telegram-бот AutoKaif успешно запущен и в сети!");
            System.out.println("Откройте Telegram и напишите своему боту /start");
            System.out.println("==================================================");
        } catch (TelegramApiException e){
            System.err.println("❌ Ошибка при запуске Telegram-бота: " + e.getMessage());
            e.printStackTrace();
        }
    }
}