package repository;

import model.Refueling;

import java.sql.Ref;
import java.util.*;

public class InMemoryRefuelingRepository implements RefuelingRepository {
    private final Map<UUID, Refueling> storage = new HashMap<>();

    @Override
    public void save(Refueling refueling) {
        if (refueling != null) {
            storage.put(refueling.getId(), refueling);
        }
    }

    @Override
    public Refueling findById(UUID id) {
        return storage.get(id);
    }

    @Override
    public List<Refueling> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Refueling> findByCarID(UUID carId) {
        List<Refueling> carRefueling = new ArrayList<>();
        for (Refueling refueling : storage.values()) {
            if (refueling.getCarId().equals(carId)) {
                carRefueling.add(refueling);
            }
        }
        return carRefueling;
    }
}

