package repository;

import model.Car;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class PostgresCarRepository implements CarRepository {
    private final DataSource dataSource;

    public PostgresCarRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Car save(Car car) {
        if (car.getId() == null) {
            car.setId(UUID.randomUUID());
        }

        String sql = """
            INSERT INTO cars (id, user_id, brand, model, year, tank_capacity, current_mileage)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET current_mileage = EXCLUDED.current_mileage;
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, car.getId());
            stmt.setLong(2, car.getUserId());
            stmt.setString(3, car.getBrand());
            stmt.setString(4, car.getModel());
            stmt.setInt(5, car.getYear());
            stmt.setDouble(6, car.getTankCapacity());
            stmt.setInt(7, car.getCurrentMileage());

            stmt.executeUpdate();
            return car;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении автомобиля", e);
        }
    }

    @Override
    public List<Car> findByUserId(long userId) {
        String sql = "SELECT * FROM cars WHERE user_id = ?";
        List<Car> cars = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cars.add(mapRowToCar(rs));
                }
            }
            return cars;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске автомобилей пользователя", e);
        }
    }

    private Car mapRowToCar(ResultSet rs) throws SQLException {
        return new Car(
                rs.getObject("id", UUID.class),
                rs.getLong("user_id"),
                rs.getString("brand"),
                rs.getString("model"),
                rs.getInt("year"),
                rs.getDouble("tank_capacity"),
                rs.getInt("current_mileage")
        );
    }

    @Override
    public Car findById(UUID id) {
        String sql = "SELECT * FROM cars WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCar(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске автомобиля по ID", e);
        }
    }

    @Override
    public List<Car> findAll() {
        String sql = "SELECT * FROM cars";
        List<Car> cars = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                cars.add(mapRowToCar(rs));
            }
            return cars;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении всех автомобилей", e);
        }
    }

    @Override
    public Car getCar() {
        String sql = "SELECT * FROM cars LIMIT 1";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return mapRowToCar(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении первого автомобиля", e);
        }
    }
}