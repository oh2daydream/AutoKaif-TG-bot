package handler;
import model.Car;
import model.Refueling;
import service.CarService;
import service.RefuelingService;

import java.time.LocalDate;

public class RefuelingAddHandler {
    private final CarService carService;
    private final RefuelingService refuelingService;
    private boolean active = false;
    private int step = 0;
    private Car targetCar;
    private int mileage;
    private double liters;


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
        this.step=0;
        return "⛽ Внесение заправки для: " + targetCar.getBrand() + " " + targetCar.getModel() + "\n" +
                "Текущий пробег авто: " + targetCar.getCurrentMileage() + " км.\n" +
                "Шаг 1 из 3: Введите пробег на одометре при заправке (км):\n" +
                "(Для отмены введите /cancel)";
    }
    public void reset(){
        this.active=false;
        this.step=0;
        this.targetCar=null;
        this.mileage=0;
        this.liters=0.0;
    }
    public String handle(String input){
        String message = input.trim();
        if("/cancle".equalsIgnoreCase(message)){
            reset();
            return "❌ Внесение заправки отменено.";
        }
        switch(step){
            case 0:
                try{
                    int inputMileage = Integer.parseInt(message);
                    if(inputMileage < targetCar.getCurrentMileage()){
                        return "⚠️ Ошибка: пробег не может быть меньше текущего (" +
                                targetCar.getCurrentMileage() + " км). Попробуйте снова:";
                    }
                    this.mileage = inputMileage;
                    this.step++;
                    return "Шаг 2 из 3: Введите количество заправленных литров (например, 42.5):";
                } catch (NumberFormatException e){
                    return "⚠️ Пробег должен быть целым числом. Попробуйте снова:";
                }
            case 1:
                try{
                    double inputLiters = Double.parseDouble(message.replace(',','.'));
                    if(inputLiters < 0 || inputLiters > 500 || inputLiters > targetCar.getTankCapacity()*1.1){
                        return "⚠Объём должен быть положительным числом (до 500 л) и не превышать объем бака машины. Попробуйте снова:";
                    }
                    this.liters = inputLiters;
                    this.step++;
                    return "Шаг 3 из 3: Введите цену за 1 литр в рублях (например, 64.80):";
                } catch (NumberFormatException e) {
                    return "⚠️ Литры должны быть числом. Попробуйте снова:";
                }
            case 2:
                try{
                    double pricePerLiter = Double.parseDouble(message.replace(',','.'));
                    if(pricePerLiter <=0 ){
                        return "⚠️ Цена за литр должна быть больше 0. Попробуйте снова:";
                    }
                    Refueling refueling = new Refueling(
                            targetCar.getId(),
                            this.liters,
                            pricePerLiter,
                            this.mileage,
                            LocalDate.now()
                    );
                    refuelingService.addRefueling(refueling);
                    carService.updateMileage(targetCar.getId(),this.mileage);
                    double totalCost = refueling.getTotalCost();
                    reset();
                    return String.format("✅ Заправка сохранена!\n" +
                                    "• Залито: %.2f л по цене %.2f ₽\n" +
                                    "• Итоговая сумма: %.2f ₽\n" +
                                    "• Новый пробег: %d км",
                            liters, pricePerLiter, totalCost, mileage);
                } catch (NumberFormatException e){
                    return "⚠️ Цена должна быть числом. Попробуйте снова:";
                }
            default:
                reset();
                return "⚠️ Неизвестный шаг. Сценарий сброшен.";
        }
    }

}
