package repository;

import model.Refueling;

import java.util.List;
import java.util.UUID;

public interface RefuelingRepository {
    void save(Refueling refueling);

    Refueling findById(UUID id);

    List<Refueling> findAll();

    List<Refueling> findByCarID(UUID carId);
}
