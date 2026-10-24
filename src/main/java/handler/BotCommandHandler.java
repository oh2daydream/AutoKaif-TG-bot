package handler;

import model.Car;
import model.FuelStats;
import model.Refueling;
import service.CarService;
import service.RefuelingService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.lang3.StringUtils.isNumeric;

public class BotCommandHandler {
    private final RefuelingService refuelingService;
    private final CarService carService;

    private final Map<Long, CarAddHandler> carDialogs = new ConcurrentHashMap<>();
    private final Map<Long, RefuelingAddHandler> refuelDialogs = new ConcurrentHashMap<>();

    public BotCommandHandler(RefuelingService refuelingService, CarService carService) {
        this.refuelingService = refuelingService;
        this.carService = carService;
    }

    public String handleMessage(long userId, String input) {
        if (input == null || input.trim().isEmpty()) {
            return "Пожалуйста, Введите команду или текст";
        }
        String message = input.trim();
        if ("/cancel".equalsIgnoreCase(message)) {
            boolean wasActive = carDialogs.remove(userId) != null | refuelDialogs.remove(userId) !=null;
            return wasActive ? "❌ Действие отменено." : "Нет активных действий для отмены.";

        }
        if(carDialogs.containsKey(userId)){
            CarAddHandler handler = carDialogs.get(userId);
            String response = handler.handle(message);
            if(!handler.isActive()){
                carDialogs.remove(userId);
            }
            return response;
        }
        if(refuelDialogs.containsKey(userId)){
            RefuelingAddHandler handler = refuelDialogs.get(userId);
            String response = handler.handle(message);
            if(!handler.isActive()){
                refuelDialogs.remove(userId);
            }
            return response;
        }
        return processCommand(userId, message);
    }
    private String processCommand(long userId, String command) {
        String[] tokens = command.split("\\s+");
        String mainCommand = tokens[0].toLowerCase();
        switch (mainCommand) {
            case "/start":
                return handleStart(userId);
            case "/help":
                return "ℹ️ Справка по командам:\n" +
                        "/addcar - Добавить автомобиль в гараж\n" +
                        "/garage - Посмотреть все автомобили\n" +
                        "/select <id> - Выбрать активный автомобиль (например: /select 1)\n" +
                        "/car - Карточка текущего активного авто\n" +
                        "/refuel - Записать новую заправку\n" +
                        "/stats - Статистика расхода топлива\n" +
                        "/history - Журнал чеков заправок\n" +
                        "Для выхода напишите 'exit'.";
            case "/addcar":
                CarAddHandler carHandler = new CarAddHandler(carService, userId);
                carDialogs.put(userId, carHandler);
                return  carHandler.start();
            case "/garage":
                return buildGarageMessage(userId);
            case "/select":
                return handleSelectCar(userId,tokens);
            case "/car":
                Car active = carService.getActiveCar(userId);
                return active != null ? "Текущий активный профиль:\n" + active : "Гараж пуст. Добавьте авто через /addcar.";
            case "/refuel":
                Car carForRefuel = carService.getActiveCar(userId);
                if (carForRefuel == null) {
                    return "⚠️ В вашем гараже нет машин! Сначала добавьте авто через /addcar.";
                }
                RefuelingAddHandler refuelHandler = new RefuelingAddHandler(refuelingService, carService);
                refuelDialogs.put(userId, refuelHandler);
                return refuelHandler.start(carForRefuel);
            case "/stats":
                return buildStatsMessage(userId);
            case "/history":
                return buildHistoryMessage(userId);
            default:
                return "❓ Неизвестная команда. Введите /help для просмотра списка доступных действий.";
        }
    }
    private String handleStart(long userId) {
        Car activeCar = carService.getActiveCar(userId);
        if (activeCar != null) {
            return "👋 С возвращением в AutoKaif!\n\n" +
                    "🚗 Активный автомобиль: " + activeCar.getBrand() + " " + activeCar.getModel() +
                    " (" + activeCar.getCurrentMileage() + " км)\n\n" +
                    "Доступные действия:\n" +
                    "/refuel - Записать заправку\n" +
                    "/stats - Посмотреть расход топлива\n" +
                    "/garage - Список всех машин\n" +
                    "/help - Справка";
        } else {
            return "👋 Привет! Я бот бортового журнала AutoKaif.\n\n" +
                    "Ваш гараж пока пуст. Зарегистрируйте свой первый автомобиль через команду /addcar.\n" +
                    "Чтобы посмотреть все доступные команды напишите /help";
        }
    }

    private String buildGarageMessage(long userId) {
        List<Car> cars = carService.getCarsByUserId(userId);
        if (cars.isEmpty()) {
            return "Гараж пуст. Добавьте автомобиль командой /addcar.";
        }
        Car activeCar = carService.getActiveCar(userId);
        StringBuilder sb = new StringBuilder("🚗 Ваш автопарк:\n");
        int index = 1;
        for (Car car : cars) {
            sb.append("• [ID ").append(index++).append(" UUID: ").append(car.getId()).append("] ")
                    .append(car.getBrand()).append(" ").append(car.getModel())
                    .append(" (").append(car.getCurrentMileage()).append(" км)");

            if (activeCar != null && activeCar.getId() != null && activeCar.getId().equals(car.getId())) {
                sb.append(" ⭐️ [АКТИВЕН]");
            }
            sb.append("\n");
        }
        sb.append("\nЧтобы переключить машину, напишите: /select <ID>");
        return sb.toString();
    }

    private String handleSelectCar(long userId, String[] tokens) {
        List<Car> cars = carService.getCarsByUserId(userId);
        if(cars.isEmpty()){
            return "Гараж пуст. Сначала добавьте автомобиль через /addcar.";
        }
        if (tokens.length < 2) {
            return "⚠️ Укажите ID автомобиля. Например: /select 1\nСписок автомобилей: /garage";
        }
        String arg = tokens[1].trim();
        if (isNumeric(arg)) {
            int index = Integer.parseInt(arg);
            if (index < 1 || index > cars.size()) {
                return "❌ Автомобиля под номером " + index + " нет в гараже (всего машин: " + cars.size() + ").\nПроверьте список: /garage";
            }
            Car selected = cars.get(index - 1);
            carService.setActiveCar(userId, selected.getId());
            return "✅ Активным автомобилем выбран: " + selected.getBrand() + " " + selected.getModel() + "!";
        }
        try{
            UUID selectedId = UUID.fromString(arg);
            for(Car car:cars){
                if(car.getId().equals(selectedId)){
                    carService.setActiveCar(userId, car.getId());
                    return "✅ Активным автомобилем выбран: " + car.getBrand() + " " + car.getModel() + "!";
                }
            }
            return "❌ Автомобиль с таким ID не найден.";
        } catch (IllegalArgumentException e){
            return "⚠️ Некорректный номер. Введите цифру из списка /garage. Пример: /select 1";
        }
    }

    private String buildStatsMessage(long userId) {
        Car activeCar = carService.getActiveCar(userId);
        if (activeCar == null) {
            return "Гараж пуст. Сначала зарегистрируйте автомобиль через /addcar";
        }
        FuelStats stats = refuelingService.calculateFuelStats(activeCar);
        if (stats == null) {
            return "📊 История заправок для [" + activeCar.getBrand() + " " + activeCar.getModel() + "] пока пуста.\n" +
                    "Добавьте первый чек через команду /refuel.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("📊 СТАТИСТИКА: ").append(activeCar.getBrand()).append(" ").append(activeCar.getModel()).append("\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("⛽ ЗАПРАВКИ: ").append(stats.totalRefuelings)
                .append(" (полных: ").append(stats.fullTankCount)
                .append(", частичных: ").append(stats.partialTankCount).append(")\n");
        if (stats.avgDaysBetweenRefuels > 0) {
            sb.append(String.format("🗓 ПЕРИОДИЧНОСТЬ: в ср. раз в %.1f дн. (посл: %d дн.)\n", stats.avgDaysBetweenRefuels, stats.lastDaysBetweenRefuels));
        }
        if (stats.totalRefuelings >= 2) {
            sb.append(String.format("🛣 НА ОДНОМ БАКЕ: в ср. %.1f км (посл: %d км)\n\n", stats.avgDistance, stats.lastDistance));
        } else {
            sb.append("\n");
        }
        sb.append("🛢 ТОПЛИВО:\n");
        sb.append(String.format("• Всего залито:  %.2f л (в ср. %.2f л, посл: %.2f л)\n", stats.totalLiters, stats.avgLiters, stats.lastLiters));
        sb.append(String.format("• Цена за литр:  в ср. %.2f ₽ (посл: %.2f ₽)\n\n", stats.avgPricePerLiter, stats.lastPricePerLiter));
        sb.append("💰 РАСХОДЫ:\n");
        sb.append(String.format("• Всего трат:    %.2f ₽ (ср. чек: %.2f ₽, посл: %.2f ₽)\n", stats.totalCost, stats.avgCost, stats.lastCost));
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        if (stats.avgConsumption > 0) {
            sb.append(String.format("⭐️ Средний расход: %.2f л/100 км (%.2f км/л)\n", stats.avgConsumption, stats.avgKmPerLiter));
            sb.append(String.format("₽ Стоимость километра (последней заправки): %2f руб/км\n", stats.lastCostPerKm));
            sb.append(String.format("₽ Стоимость километра (за все время): %2f руб/км\n", stats.avgCostPerKm));
        } else {
            sb.append("ℹ️ Средний расход: требуется от 2 заправок «до полного бака».\n");
        }
        if (stats.lastConsumption > 0) {
            sb.append(String.format("🏁 Последний бак:  %.2f л/100 км (%.2f км/л)\n", stats.lastConsumption, stats.lastKmPerLiter));
        }
        return sb.toString();
    }

    private String buildHistoryMessage(long userId) {
        Car activeCar = carService.getActiveCar(userId);
        if (activeCar == null) {
            return "Гараж пуст. Сначала зарегистрируйте автомобиль через /addcar";
        }
        List<Refueling> records = refuelingService.getAllRefuelingsByCarId(activeCar.getId());
        if (records.isEmpty()) {
            return "Журнал заправок автомобиля [" + activeCar.getBrand() + " " + activeCar.getModel() + " ] пуст.";
        }
        StringBuilder sb = new StringBuilder("📋 ЖУРНАЛ ЗАПРАВОК:\n");
        for (int i = 0; i < records.size(); i++) {
            sb.append(i + 1).append(". ").append(records.get(i).toString()).append("\n");
        }
        return sb.toString();
    }

    public boolean isDialogActive(long userId){
        return carDialogs.containsKey(userId)||refuelDialogs.containsKey(userId);
    }
    public Car getActiveCar(long userId) {
        return carService.getActiveCar(userId);
    }
    public boolean hasCars(long userId) {
        return carService.hasCar(userId);
    }
    public List<Car> getUserCars(long userId) {
        return carService.getCarsByUserId(userId);
    }
}
