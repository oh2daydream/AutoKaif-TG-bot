package repository;

import model.Refueling;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class PostgresRefuelingRepository implements RefuelingRepository {
    private final DataSource dataSource;

    public PostgresRefuelingRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Refueling refueling) {
        String sql = """
            INSERT INTO refuelings (id, car_id, mileage, liters, total_cost, date, is_full_tank)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                mileage = EXCLUDED.mileage,
                liters = EXCLUDED.liters,
                total_cost = EXCLUDED.total_cost,
                date = EXCLUDED.date,
                is_full_tank = EXCLUDED.is_full_tank;
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, refueling.getId());
            stmt.setObject(2, refueling.getCarId());
            stmt.setInt(3, refueling.getMileage());
            stmt.setDouble(4, refueling.getLiters());
            stmt.setDouble(5, refueling.getTotalCost());
            stmt.setDate(6, java.sql.Date.valueOf(refueling.getDate()));
            stmt.setBoolean(7, refueling.isFullTank());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении заправки", e);
        }
    }

    @Override
    public Refueling findById(UUID id) {
        String sql = "SELECT * FROM refuelings WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToRefueling(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске заправки по ID", e);
        }
    }

    @Override
    public List<Refueling> findAll() {
        String sql = "SELECT * FROM refuelings";
        List<Refueling> list = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToRefueling(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении всех заправок", e);
        }
    }

    @Override
    public List<Refueling> findByCarID(UUID carId) {
        String sql = "SELECT * FROM refuelings WHERE car_id = ?";
        List<Refueling> list = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, carId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToRefueling(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске заправок автомобиля", e);
        }
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