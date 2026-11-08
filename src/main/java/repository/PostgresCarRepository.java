package repository;

import model.Car;
import org.hibernate.SessionFactory;

import java.sql.*;
import java.util.*;

public class PostgresCarRepository implements CarRepository {
    private final SessionFactory sessionFactory;

    public PostgresCarRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Car save(Car car) {
        if (car.getId() == null) {
            car.setId(UUID.randomUUID());
        }
        return sessionFactory.fromTransaction(session -> {
            return session.merge(car);
        });
    }

    @Override
    public List<Car> findByUserId(long userId) {
        return sessionFactory.fromTransaction(session -> {
            String hql = "FROM Car c WHERE c.userId = :userId";
            return session.createSelectionQuery(hql, Car.class).getResultList();
        });
    }

    @Override
    public Car findById(UUID id) {
        return sessionFactory.fromTransaction(session -> {
            return session.get(Car.class, id);
        });
    }

    @Override
    public List<Car> findAll() {
        return sessionFactory.fromTransaction(session -> {
            return session.createSelectionQuery("FROM Car", Car.class).getResultList();
        });
    }
}