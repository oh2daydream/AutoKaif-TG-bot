package repository;

import model.Car;
import java.util.UUID;
import java.util.List;

public interface CarRepository {
    Car save(Car car);
    Car getCar();
    Car findById(UUID id);
    List<Car> findAll();
}
