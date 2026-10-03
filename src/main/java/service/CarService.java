package service;
import model.Car;

public class CarService {
    private Car currentCar;

    //методы-------------------
    public Car registerCar(String brand, String model, int year, double tankCapacity, int currentMileage) {
        this.currentCar = new Car(brand, model, year, tankCapacity, currentMileage);
        return this.currentCar;
    }
    //геттер-------------------
    public Car getCar() {
        return this.currentCar;
    }
    //проверки-------------------
    public boolean hasCar() { //чтобы не давать заправляться или обновлять пробег, пока машина не создана
        return currentCar != null;
    }
    public boolean updateMileage(int newMileage) {
        if (currentCar == null) return false;
        return currentCar.updateMileage(newMileage);
    }

}
