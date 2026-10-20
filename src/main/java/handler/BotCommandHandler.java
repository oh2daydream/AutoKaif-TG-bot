package handler;

import model.Car;
import model.FuelStats;
import model.Refueling;
import service.CarService;
import service.RefuelingService;

import java.util.List;
import java.util.UUID;

public class BotCommandHandler {
    private final RefuelingService refuelingService;
    private final CarService carService;
    private final RefuelingAddHandler refuelingAddHandler;
    private final CarAddHandler carAddHandler;
    private Car activeCar;

    public BotCommandHandler(RefuelingService refuelingService, CarService carService, RefuelingAddHandler refuelingAddHandler, CarAddHandler carAddHandler) {
        this.refuelingService = refuelingService;
        this.carService = carService;
        this.refuelingAddHandler = refuelingAddHandler;
        this.carAddHandler = carAddHandler;
        if (carService.hasCar()) {
            this.activeCar = carService.getCar();
        }
    }

    public String handleMessage(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "Пожалуйста, Введите команду или текст";
        }
        String message = input.trim();
        if ("/cancel".equalsIgnoreCase(message)) {
                carAddHandler.reset();
                refuelingAddHandler.reset();
            return "❌ Действие отменено.";
        }
        if (carAddHandler != null && carAddHandler.isActive()) {
            return carAddHandler.handle(message);
        }
        //Это проверка на идет ли заправка
        if (refuelingAddHandler.isActive()) {
            return refuelingAddHandler.handle(message);
        }
        return processCommand(message);
    }

    private String handleStart() {
        ensureActiveCar();
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

    private String buildGarageMessage() {
        ensureActiveCar();
        List<Car> cars = carService.getAllCars();
        if (cars.isEmpty()) {
            return "Гараж пуст. Добавьте автомобиль командой /addcar.";
        }
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

    private String handleSelectCar(String[] tokens) {
        List<Car> cars = carService.getAllCars();
        if(cars.isEmpty()){
            return "Гараж пуст. Сначала добавьте автомобиль через /addcar.";
        }
        if (tokens.length < 2) {
            return "⚠️ Укажите ID автомобиля. Например: /select 1\nСписок автомобилей: /garage";
        }
        String arg = tokens[1].trim();
        try{
            int index = Integer.parseInt(arg);
            if(index<1 || index > cars.size()){
                return "❌ Автомобиля под номером " + index + " нет в гараже (всего машин: " + cars.size() + ").\nПроверьте список: /garage";
            }
            this.activeCar = cars.get(index-1);
            return "✅ Активным автомобилем выбран: " + activeCar.getBrand() + " " + activeCar.getModel() + "!";
        } catch (NumberFormatException ignored){

        }
        try{
            UUID selectedId = UUID.fromString(arg);
            for(Car car:cars){
                if(car.getId().equals(selectedId)){
                    this.activeCar = car;
                    return "✅ Активным автомобилем выбран: " + car.getBrand() + " " + car.getModel() + "!";
                }
            }
            return "❌ Автомобиль с таким ID не найден.";
        } catch (IllegalArgumentException e){
            return "⚠️ Некорректный номер. Введите цифру из списка /garage. Пример: /select 1";
        }
    }

    private String buildStatsMessage() {
        ensureActiveCar();
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

    private String buildHistoryMessage() {
        ensureActiveCar();
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

    private void ensureActiveCar(){
        if(this.activeCar == null && carService.hasCar()){
            this.activeCar = carService.getCar();
        }
    }
    public boolean isDialogActive(){
        return (carAddHandler!=null && carAddHandler.isActive()) || (refuelingAddHandler!=null && refuelingAddHandler.isActive());
    }

    private String processCommand(String command) {
        String[] tokens = command.split("\\s+");
        String mainCommand = tokens[0].toLowerCase();
        switch (mainCommand) {
            case "/start":
                return handleStart();
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
                return carAddHandler.start();
            case "/garage":
                return buildGarageMessage();
            case "/select":
                return handleSelectCar(tokens);
            case "/car":
                ensureActiveCar();
                if (activeCar == null) {
                    return "Гараж пуст. Добавьте машину через /addcar.";
                }
                return "Текущий активный профиль:\n" + activeCar;
            case "/refuel":
                ensureActiveCar();
                if (activeCar == null) {
                    return "⚠️ Нельзя добавить заправку: в гараже нет автомобилей!\n" +
                            "Сначала добавьте машину командой /addcar.";
                }
                return refuelingAddHandler.start(activeCar);
            case "/stats":
                return buildStatsMessage();
            case "/history":
                return buildHistoryMessage();
            default:
                return "❓ Неизвестная команда. Введите /help для просмотра списка доступных действий.";
        }
    }

    public Car getActiveCar() {
        ensureActiveCar();
        return activeCar;
    }
}
