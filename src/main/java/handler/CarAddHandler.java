package handler;

import model.Car;
import service.CarService;

import java.time.Year;

public class CarAddHandler {
    private final CarService carService;
    private boolean active = false;
    private int step = 0;

    // Буферные переменные
    private String brand;
    private String model;
    private int year;
    private double tankCapacity;
    private int currentMileage;

    public CarAddHandler(CarService carService) {
        this.carService = carService;
    }

    public boolean isActive() {
        return active;
    }

    public String start() {
        this.active = true;
        this.step = 0;
        return "🚗 РЕГИСТРАЦИЯ АВТОМОБИЛЯ\n" +
                "Шаг 1 из 5: Введите марку автомобиля (например: Volkswagen):";
    }

    public void reset() {
        this.active = false;
        this.step = 0;
        this.brand = null;
        this.model = null;
        this.year = 0;
        this.tankCapacity = 0.0;
        this.currentMileage = 0;
    }

    public String handle(String input) {
        String message = input.trim();

        switch (step) {
            case 0:
                if (message.isEmpty()) {
                    return "⚠️ Марка не может быть пустой. Попробуйте снова:";
                }
                this.brand = message;
                this.step++;
                return "Шаг 2 из 5: Введите модель автомобиля (например: Polo):";

            case 1:
                if (message.isEmpty()) {
                    return "⚠️ Модель не может быть пустой. Попробуйте снова:";
                }
                this.model = message;
                this.step++;
                return "Шаг 3 из 5: Введите год выпуска (например: 2018):";

            case 2:
                try {
                    int inputYear = Integer.parseInt(message);
                    int currentYear = Year.now().getValue();
                    if (inputYear < 1900 || inputYear > currentYear) {
                        return "⚠️ Некорректный год выпуска (от 1900 до " + currentYear + "). Попробуйте снова:";
                    }
                    this.year = inputYear;
                    this.step++;
                    return "Шаг 4 из 5: Введите паспортный объём топливного бака в литрах (например: 55):";
                } catch (NumberFormatException e) {
                    return "⚠️ Год должен быть целым числом. Попробуйте снова:";
                }

            case 3:
                try {
                    double inputCapacity = Double.parseDouble(message.replace(',', '.'));
                    if (inputCapacity <= 0 || inputCapacity > 300) {
                        return "⚠️ Объём бака должен быть от 1 до 300 литров. Попробуйте снова:";
                    }
                    this.tankCapacity = inputCapacity;
                    this.step++;
                    return "Шаг 5 из 5: Введите текущий пробег автомобиля в км (например: 120000):";
                } catch (NumberFormatException e) {
                    return "⚠️ Объём бака должен быть числом. Попробуйте снова:";
                }

            case 4:
                try {
                    int inputMileage = Integer.parseInt(message);
                    if (inputMileage < 0) {
                        return "⚠️ Пробег не может быть отрицательным. Попробуйте снова:";
                    }
                    this.currentMileage = inputMileage;

                    // Все данные собраны — создаем и сохраняем машину
                    Car newCar = carService.registerCar(brand, model, year, tankCapacity, currentMileage);
                    reset();

                    return "✅ Автомобиль успешно добавлен в гараж!\n\n" + newCar.toString();

                } catch (NumberFormatException e) {
                    return "⚠️ Пробег должен быть целым числом. Попробуйте снова:";
                }

            default:
                reset();
                return "⚠️ Произошла ошибка сценария. Попробуйте снова запустить /addcar.";
        }
    }
}