package repository;
import model.Car;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

public class InMemoryCarRepository implements CarRepository {
    private final Map<UUID, Car> storage = new HashMap<>();

    @Override
    public Car save(Car car) {
        if (car.getId() == null) {
            car.setId(UUID.randomUUID());
        }
        storage.put(car.getId(), car);
        return car;
    }

    @Override
    public Car getCar() {
        if (storage.isEmpty()) {
            return null;
        }
        return storage.values().iterator().next();
    }
    @Override
    public Car findById(UUID id) {
        return storage.get(id);
    }

    @Override
    public List<Car> findAll() {
        return new ArrayList<>(storage.values());
    }
}
