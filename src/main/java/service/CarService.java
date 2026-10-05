package service;
import model.Car;
import repository.CarRepository;

import java.util.List;

public class CarService {
    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    //методы-------------------
    public Car registerCar(String brand, String model, int year, double tankCapacity, int currentMileage) {
        Car car = new Car(brand, model, year, tankCapacity, currentMileage);
        return carRepository.save(car);
    }
    //геттер-------------------
    public Car getCar() {
        return carRepository.getCar();
    }
    //проверки-------------------
    public boolean hasCar() { //чтобы не давать заправляться или обновлять пробег, пока машина не создана
        return carRepository.getCar() != null;
    }
    public boolean updateMileage(int carId,int newMileage) {
        for(Car car : carRepository.findAll()){
            if(car.getId()==carId){
                boolean updated = car.updateMileage(newMileage);
                if (updated){
                    carRepository.save(car);
                }
                return true;
            }
        }
        return false;
    }
    public List<Car> getAllCars(){
        return carRepository.findAll();
    }
}
