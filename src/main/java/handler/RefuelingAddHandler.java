package handler;

import model.Car;
import model.Refueling;
import service.CarService;
import service.RefuelingService;

import java.time.LocalDate;
import java.util.UUID;

public class RefuelingAddHandler {
    private final CarService carService;
    private final RefuelingService refuelingService;
    private boolean active = false;
    private int step = 0;
    private Car targetCar;
    private int mileage;
    private double liters;
    private boolean isFull;

    public RefuelingAddHandler(RefuelingService refuelingService, CarService carService){
        this.refuelingService = refuelingService;
        this.carService = carService;
    }

    public boolean isActive(){
        return active;
    }

    public String start(Car activeCar){
        if(activeCar == null){
            return "⚠️ Сначала выберите автомобиль из гаража с помощью команды /select <ID>!";
        }
        this.targetCar = activeCar;
        this.active = true;
        this.step = 0;
        return "⛽ Внесение заправки для: " + targetCar.getBrand() + " " + targetCar.getModel() + "\n" +
                "Текущий пробег авто: " + targetCar.getCurrentMileage() + " км.\n" +
                "Шаг 1 из 4: Введите пробег на одометре при заправке (км):\n" +
                "(Для отмены введите /cancel)";
    }

    public void reset(){
        this.active = false;
        this.step = 0;
        this.targetCar = null;
        this.mileage = 0;
        this.liters = 0.0;
        this.isFull = false;
    }

    public String handle(String input){
        String message = input.trim();
        if("/cancel".equalsIgnoreCase(message)){
            reset();
            return "❌ Внесение заправки отменено.";
        }

        switch(step){
            case 0: // Шаг 1: Пробег
                try{
                    int inputMileage = Integer.parseInt(message);
                    if(inputMileage < targetCar.getCurrentMileage()){
                        return "⚠️ Ошибка: пробег не может быть меньше текущего (" +
                                targetCar.getCurrentMileage() + " км). Попробуйте снова:";
                    }
                    this.mileage = inputMileage;
                    this.step++;
                    return "Шаг 2 из 4: Введите количество заправленных литров (например, 42.5):";
                } catch (NumberFormatException e){
                    return "⚠️ Пробег должен быть целым числом. Попробуйте снова:";
                }

            case 1: // Шаг 2: Литры
                try{
                    double inputLiters = Double.parseDouble(message.replace(',','.'));
                    if(inputLiters <= 0 || inputLiters > 500 || (targetCar.getTankCapacity() > 0 && inputLiters > targetCar.getTankCapacity()*1.1)){
                        return "⚠️ Объём должен быть положительным числом (до 500 л) и не превышать объём бака машины. Попробуйте снова:";
                    }
                    this.liters = inputLiters;
                    this.step++;
                    return "Шаг 3 из 4: Полный ли это бак? Напишите «да» или «нет»:";
                } catch (NumberFormatException e) {
                    return "⚠️ Литры должны быть числом. Попробуйте снова:";
                }

            case 2: // Шаг 3: Полный бак (да/нет)
                String answer = message.toLowerCase();
                if("да".equals(answer) || "д".equals(answer)){
                    this.isFull = true;
                    this.step++;
                    return "Шаг 4 из 4: Введите цену за 1 литр в рублях (например, 64.80):";
                } else if("нет".equals(answer) || "н".equals(answer)){
                    this.isFull = false;
                    this.step++;
                    return "Шаг 4 из 4: Введите цену за 1 литр в рублях (например, 64.80):";
                } else {
                    return "⚠️ Не понял ответ. Пожалуйста, напишите чётко «да» или «нет»:";
                }

            case 3:
                try{
                    double pricePerLiter = Double.parseDouble(message.replace(',','.'));
                    if(pricePerLiter <= 0 ){
                        return "⚠️ Цена за литр должна быть больше 0. Попробуйте снова:";
                    }

                    double totalCost = this.liters * pricePerLiter;
                    Refueling refueling = new Refueling(
                            targetCar.getId(),
                            this.mileage,
                            this.liters,
                            totalCost,
                            LocalDate.now(),
                            this.isFull
                    );

                    refuelingService.addRefueling(refueling);
                    carService.updateMileage(targetCar.getId(), this.mileage);

                    // Сохраняем локальные копии для красивого вывода перед сбросом
                    int savedMileage = this.mileage;
                    double savedLiters = this.liters;
                    boolean savedIsFull = this.isFull;

                    reset();

                    return String.format("✅ Заправка успешно сохранена!\n" +
                                    "• Полный бак: %s\n" +
                                    "• Залито: %.2f л по цене %.2f ₽\n" +
                                    "• Итоговая сумма: %.2f ₽\n" +
                                    "• Новый пробег: %d км",
                            savedIsFull ? "Да" : "Нет", savedLiters, pricePerLiter, totalCost, savedMileage);
                } catch (NumberFormatException e){
                    return "⚠️ Цена должна быть числом. Попробуйте снова:";
                }

            default:
                reset();
                return "⚠️ Неизвестный шаг. Сценарий сброшен.";
        }
    }
}