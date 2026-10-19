import handler.BotCommandHandler;
import handler.CarAddHandler;
import handler.RefuelingAddHandler;

import repository.CarRepository;
import repository.InMemoryCarRepository;
import repository.InMemoryRefuelingRepository;
import repository.RefuelingRepository;
import service.CarService;
import service.RefuelingService;

import java.util.Scanner;

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

        Scanner scanner = new Scanner(System.in);

        System.out.println("==================================================");
        System.out.println("🤖 Консольный бот AutoKaif запущен и готов к работе!");
        System.out.println("Напишите /start для начала или 'exit' для выхода.");
        System.out.println("==================================================\n");

        while (true) {
            System.out.println("Вы :");
            String userInput = scanner.nextLine();

            if ("exit".equalsIgnoreCase(userInput.trim()) || "выход".equalsIgnoreCase(userInput.trim())) {
                System.out.println("\nБот: До встречи! Хороших дорог и полного бака 🚗💨");
                break;
            }

            String botResponse = botHandler.handleMessage(userInput);
            System.out.println("Бот: " + botResponse + "\n");
        }
        scanner.close();
    }
}