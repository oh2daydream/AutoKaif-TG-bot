package repository;
import model.Car;

public interface CarRepository {
    Car save(Car car);
    Car getCar();
}
