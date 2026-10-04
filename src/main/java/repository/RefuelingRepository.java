package repository;
import model.Refueling;
import java.util.List;
public interface RefuelingRepository {
    Refueling save(Refueling refueling);
    List<Refueling> findAll();
}
