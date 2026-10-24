package service;

import model.Car;
import repository.CarRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CarService {
    private final CarRepository carRepository;

    private final Map<Long, UUID> userActiveCars =  new ConcurrentHashMap<>();
    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    //методы-------------------
    public Car registerCar(String brand,long userId, String model, int year, double tankCapacity, int currentMileage) {
        Car car = new Car(brand, userId, model, year, tankCapacity, currentMileage);
        Car savedCar = carRepository.save(car);
        return savedCar;
    }

    public List<Car> getCarsByUserId(long userId){
        return carRepository.findByUserId(userId);
    }
    //геттер-------------------
    public Car getCar() {
        return carRepository.getCar();
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    //проверки-------------------
    public boolean hasCar(long userId) { //чтобы не давать заправляться или обновлять пробег, пока машина не создана
        return carRepository.findByUserId(userId) != null;
    }

    public Car getActiveCar(long userId) {
        UUID activeCarId = userActiveCars.get(userId);
        if (activeCarId != null) {
            Car car = carRepository.findById(activeCarId);
            if (car != null) return car;
        }
        List<Car> userCars = getCarsByUserId(userId);
        if (!userCars.isEmpty()) {
            Car defaultCar = userCars.get(0);
            userActiveCars.put(userId, defaultCar.getId());
            return defaultCar;
        }
        return null;
    }
    public boolean updateMileage(UUID carId, int newMileage) {
        Car car = carRepository.findById(carId);
        if (car == null) return false;
        boolean updated = car.updateMileage(newMileage);
        if (updated) {
            carRepository.save(car);
        }
        return updated;
    }
    public void setActiveCar(long userId, UUID carId){
        if(carId!=null && carRepository.findById(carId)!=null){
            userActiveCars.put(userId, carId);
        }
    }
    public Car getCarById(UUID id) {
        return carRepository.findById(id);
    }
}
