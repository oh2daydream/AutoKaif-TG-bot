package repository;

import model.Refueling;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class PostgresRefuelingRepository implements RefuelingRepository {
    private final SessionFactory sessionFactory;

    public PostgresRefuelingRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(Refueling refueling) {
        sessionFactory.inTransaction(session -> {session.merge(refueling);});
    }

    @Override
    public Refueling findById(UUID id) {
        return sessionFactory.fromTransaction(session -> {
            return session.get(Refueling.class,id);
        });
    }

    @Override
    public List<Refueling> findAll() {
        return  sessionFactory.fromTransaction(session -> {
            return session.createSelectionQuery("FROM Refueling", Refueling.class).getResultList();
        });
    }

    @Override
    public List<Refueling> findByCarID(UUID carId) {
        return sessionFactory.fromTransaction(session -> {
            String hql = "FROM Refueilng r WHERE r.carId = :carId ORDER BY r.mileage ASC";

            return session.createSelectionQuery(hql, Refueling.class).setParameter("carId", carId).getResultList();
        });
    }

    private Refueling mapRowToRefueling(ResultSet rs) throws SQLException {
        return new Refueling(
                rs.getObject("id", UUID.class),
                rs.getObject("car_id", UUID.class),
                rs.getInt("mileage"),
                rs.getDouble("liters"),
                rs.getDouble("total_cost"),
                rs.getDate("date").toLocalDate(),
                rs.getBoolean("is_full_tank")
        );
    }
}