package repository;

import model.Car;

import java.util.List;
import java.util.UUID;

public interface CarRepository {
    Car save(Car car);

    Car getCar();

    Car findById(UUID id);

    List<Car> findAll();
}
