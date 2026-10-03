package handler;

import model.Car;
import model.Refueling;
import model.FuelStats;
import service.CarService;
import service.RefuelingService;

import java.time.LocalDate;
import java.util.List;

public class BotCommandHandler {
    private final RefuelingService refuelingService;
    private final CarService carService;

    private Car activeCar;

    public BotCommandHandler(RefuelingService refuelingService, CarService carService) {
        this.refuelingService = refuelingService;
        this.carService = carService;

        if(carService.hasCar()){
            this.activeCar=carService.getCar();
        }
    }

    private enum State{
        IDLE,
        // Шаги машины
        WAITING_CAR_BRAND,
        WAITING_CAR_MODEL,
        WAITING_CAR_YEAR,
        WAITING_CAR_TANK,
        WAITING_CAR_MILEAGE,
        // Шаги заправки
        WAITING_REFUEL_MILEAGE,
        WAITING_REFUEL_LITERS,
        WAITING_REFUEL_COST,
        WAITING_REFUEL_IS_FULL;
    }
    private State currentState = State.IDLE;
    private int tempRefuelMileage;
    private double tempRefuelLiters;
    private double tempCost;
    private String tempBrand;
    private String tempModel;
    private int tempYear;
    private double tempTank;


    public String handleMessage(String input){
        if (input == null || input.trim().isEmpty()){
            return "Пожалуйста, Введите команду или текст";
        }
        String message = input.trim();
        if("/cancle".equalsIgnoreCase(message)){
            currentState = State.IDLE;
            return "❌ Действие отменено.";
        } switch (currentState) {
            case IDLE:
                return processCommand(message);
            case WAITING_CAR_BRAND:
                return processCarBrand(message);
            case WAITING_CAR_MODEL:
                return processCarModel(message);
            case WAITING_CAR_YEAR:
                return processCarYear(message);
            case WAITING_CAR_TANK:
                return processCarTank(message);
            case WAITING_CAR_MILEAGE:
                return processCarMileage(message);
            case WAITING_REFUEL_MILEAGE:
                return processRefuelMileage(message);
            case WAITING_REFUEL_LITERS:
                return processRefuelLiters(message);
            case WAITING_REFUEL_COST:
                return processRefuelCost(message);
            case WAITING_REFUEL_IS_FULL:
                    return processRefuelIsFull(message);
            default:
                currentState = State.IDLE;
                return "Неизвестное состояние диалога. Возврат в главное меню.";
        }
    }
    private String handleStart(){
        if(activeCar!=null){
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
                    "Ваш гараж пока пуст. Зарегистрируйте свой первый автомобиль через команду /addcar.";
        }
    }
    private String buildGarageMessage(){
        List<Car> cars= carService.getAllCars();
        if(cars.isEmpty()){
            return "Гараж пуст. Добавьте автомобиль командой /addcar.";
        }
        StringBuilder sb =new StringBuilder("🚗 Ваш автопарк:\n");
        for (Car car : cars) {
            sb.append("• [ID: ").append(car.getId()).append("] ")
                    .append(car.getBrand()).append(" ").append(car.getModel())
                    .append(" (").append(car.getCurrentMileage()).append(" км)");

            if (activeCar != null && activeCar.getId() == car.getId()) {
                sb.append(" ⭐️ [АКТИВЕН]");
            }
            sb.append("\n");
        }
        sb.append("\nЧтобы переключить машину, напишите: /select <ID>");
        return sb.toString();
    }
    private String handleSelectCar(String[] tokens){
        if (tokens.length < 2){
            return "⚠️ Укажите ID автомобиля. Например: /select 1\nСписок автомобилей: /garage";
        }
        try {
            int selectedId = Integer.parseInt(tokens[1]);
            List<Car> cars = carService.getAllCars();
            for(Car car :cars){
                if(car.getId() == selectedId){
                    this.activeCar = car;
                    return "✅ Активным автомобилем выбран: " + car.getBrand() + " " + car.getModel() + "!";
                }
            }
            return "❌ Автомобиль с ID " + selectedId + " не найден в гараже. Проверьте список через /garage.";
        }   catch (NumberFormatException e){
            return "⚠️ Некорректный ID. Введите число. Пример: /select 1";
        }
    }
    private String processCarBrand(String message){
        this.tempBrand = message;
        currentState = State.WAITING_CAR_MODEL;
        return "Шаг 2 из 5: Введите модель (например, Polo):";
    }
    private String processCarModel(String message){
        this.tempModel =  message;
        currentState = State.WAITING_CAR_YEAR;
        return "Шаг 3 из 5: Введите год выпуска (например, 2018):";
    }
    private String processCarYear(String message){
        try {
            int year = Integer.parseInt(message);
            if (year < 1900 || year > 2030) {
                return "⚠️ Неправдоподобный год выпуска. Попробуйте снова:";
            }
            this.tempYear = year;
            currentState = State.WAITING_CAR_TANK;
            return "Шаг 4 из 5: Введите объём топливного бака в литрах (например, 55):";
        } catch (NumberFormatException e) {
            return "⚠️ Год должен быть целым числом. Попробуйте снова:";
        }
    }
    private String processCarTank(String message){
        try {
            double tank = Double.parseDouble(message.replace(',', '.'));
            if (tank <= 0 || tank > 500) {
                return "⚠️ Объём бака должен быть от 1 до 500 литров. Попробуйте снова:";
            }
            this.tempTank = tank;
            currentState = State.WAITING_CAR_MILEAGE;
            return "Шаг 5 из 5: Введите текущий пробег в километрах (например, 120000):";
        } catch (NumberFormatException e) {
            return "⚠️ Объём бака должен быть числом. Попробуйте снова:";
        }
    }
    private String processCarMileage(String message){
        try {
            int mileage = Integer.parseInt(message);
            if (mileage < 0) {
                return "⚠️️ Пробег не может быть отрицательным. Попробуйте снова:";
            }

            // Финал: все данные собраны, создаём объект Car
            Car newCar = new Car(tempBrand, tempModel, tempYear, tempTank, mileage);
            Car savedCar = carService.registerCar(tempBrand, tempModel, tempYear, tempTank, mileage);

            this.activeCar = (savedCar != null) ? savedCar : newCar;
            currentState = State.IDLE;

            return "🎉 Автомобиль " + tempBrand + " " + tempModel + " (" + tempYear + ") успешно добавлен!\n" +
                    "Он выбран как активный. Теперь можно вносить заправки командой /refuel.";
        } catch (NumberFormatException e) {
            return "⚠️ Пробег должен быть целым числом. Попробуйте снова:";
        }
    }
    private String processRefuelMileage(String message){
        try{
            int mileage = Integer.parseInt(message);
            if(mileage < activeCar.getCurrentMileage()){
                return "⚠️ Ошибка: пробег не может быть меньше текущего пробега авто (" +
                        activeCar.getCurrentMileage() + " км). Попробуйте снова:";
            }
            this.tempRefuelMileage = mileage;
            currentState = State.WAITING_REFUEL_LITERS;
            return "Шаг 2 из 4: Введите кол-во заправленных литров(например 42,3):";
        } catch (NumberFormatException e) {
            return "⚠️ Пробег должен быть целым числом. Попробуйте снова:";
        }
    }
    private String processRefuelLiters(String message){
        try {
            double liters = Double.parseDouble(message.replace(',','.'));
            if (liters<=0){
                return "⚠️ Количество литров должно быть строго больше 0. Попробуйте снова:";
            }
            if (activeCar.getTankCapacity() > 0 && liters > activeCar.getTankCapacity() * 1.1){
                return "⚠️ Объём превышает бак авто (" + activeCar.getTankCapacity() +
                        " л). Проверьте цифру и введите снова:";
            }
            this.tempRefuelLiters = liters;
            currentState = State.WAITING_REFUEL_COST;
            return "Шаг 3  из 4: Введите общую стоимост заправки в рублях(например 2345)";
        } catch (NumberFormatException e) {
            return "⚠️ Объём топлива должен быть числом. Попробуйте снова:";
        }
    }
    private String processRefuelCost(String message){
        try{
            double cost = Double.parseDouble(message.replace(',','.'));
            if(cost <= 0){
                return "⚠️ Стоимость должна быть строго больше 0. Попробуйте снова:";
            }
            this.tempCost = cost;
            currentState = State.WAITING_REFUEL_IS_FULL;
            return "Шаг 4 из 4: Скажите вы наполнили полный бак? да/нет\n";
        } catch (NumberFormatException e){
            return "⚠️ Стоимость должна быть числом. Попробуйте снова:";
        }
    }
    private String processRefuelIsFull(String message){
        boolean isYes = "да".equalsIgnoreCase(message);
        boolean isNo = "нет".equalsIgnoreCase(message) ;
        if (!isYes && !isNo) {
            return "⚠️ Не понял ответ. Пожалуйста, напишите «да» или «нет»:\n(или /cancel для отмены)";
        }
        boolean isFull = isYes;
        Refueling refueling = new Refueling(
                activeCar.getId(),
                this.tempRefuelMileage,
                this.tempRefuelLiters,
                this.tempCost,
                LocalDate.now(),
                isFull
        );
        refuelingService.addRefueling(refueling);
        activeCar.updateMileage(this.tempRefuelMileage);
        carService.updateMileage(this.tempRefuelMileage);
        currentState = State.IDLE;
        String tankStatus= isFull ? "до полного бака" : "частичная заправка";
        return "✅ Заправка успешно сохранена (" + tankStatus + ")!\n" +
                "• Пробег: " + this.tempRefuelMileage + " км\n" +
                "• Залито: " + this.tempRefuelLiters + " л на сумму " + this.tempCost + " руб.\n\n" +
                "Посмотреть обновлённую статистику: /stats";

}

private String processCommand(String commamd){
        String[] tokens = commamd.split("\\s+");
        String mainCommand =  tokens[0].toLowerCase();
        switch (mainCommand){
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
                currentState = State.WAITING_CAR_BRAND;
                return "🚗 Добавление автомобиля.\n" +
                        "Шаг 1 из 5: Введите марку (например, Volkswagen):\n" +
                        "(для отмены напишите /cancel)";
            case "/garage":
//                return buildGarageMessage();
            case "/select":
//                return handleSelectCar(tokens);
            case "/car":
                if(activeCar==null){
                    return "Гараж пуст. Добавьте машину через /addcar.";
                }
                return "Текущий активный профиль:\n"+activeCar.toString();
            case "/refuel":
                if(activeCar==null){
                    return "⚠️ Нельзя добавить заправку: в гараже нет автомобилей!\n" +
                            "Сначала добавьте машину командой /addcar.";
                }
                currentState = State.WAITING_REFUEL_MILEAGE;
                return "⛽ Заправка для [" + activeCar.getBrand() + " " + activeCar.getModel() + "]\n" +
                        "Шаг 1 из 4: Введите текущий пробег (на одометре не менее\n" + activeCar.getCurrentMileage() + " км):\n" + "(для отмены напишите /cancle)";
            case "/stats":
//                return buildStatsMessage();
            case "/history":
//                return  buildHistoryMessage();
            default: return "❓ Неизвестная команда. Введите /help для просмотра списка доступных действий.";
        }
    }
}
