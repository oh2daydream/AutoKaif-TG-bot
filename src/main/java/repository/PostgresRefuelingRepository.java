package repository;

import model.Refueling;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.UUID;

public class PostgresRefuelingRepository implements RefuelingRepository {
    private final SessionFactory sessionFactory;

    public PostgresRefuelingRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(Refueling refueling) {
        sessionFactory.inTransaction(session -> {
            session.merge(refueling);
        });
    }

    @Override
    public Refueling findById(UUID id) {
        return sessionFactory.fromTransaction(session -> {
            return session.get(Refueling.class, id);
        });
    }

    @Override
    public List<Refueling> findAll() {
        return sessionFactory.fromTransaction(session -> {
            return session.createSelectionQuery("FROM Refueling", Refueling.class).getResultList();
        });
    }

    @Override
    public List<Refueling> findByCarID(UUID carId) {
        return sessionFactory.fromTransaction(session -> {
            String hql = "FROM Refueling r WHERE r.carId = :carId ORDER BY r.mileage ASC";

            return session.createSelectionQuery(hql, Refueling.class).setParameter("carId", carId).getResultList();
        });
    }
}