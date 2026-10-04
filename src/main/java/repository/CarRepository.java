package repository;
import model.Car;

import java.util.List;

public interface CarRepository {
    Car save(Car car);
    Car getCar();
    List<Car> findAll();

}
