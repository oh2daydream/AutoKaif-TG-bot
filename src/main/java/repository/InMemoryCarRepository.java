package repository;
import model.Car;
import java.util.HashMap;
import java.util.Map;

public class InMemoryCarRepository implements CarRepository {
    private final Map<Integer, Car> storage = new HashMap<>();
    private int currentId = 1;

    @Override
    public Car save(Car car) {
        if (car.getId() == 0) {
            car.setId(currentId++);
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
}
