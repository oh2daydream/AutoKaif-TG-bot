package model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
@Entity
@Table(name = "refueling")
public class Refueling {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "car_id", nullable = false)
    private UUID carId;
    @Column(name = "mileage", nullable = false)
    private int mileage;
    @Column(name = "liters", nullable = false)
    private double liters;
    @Column(name = "total_cost", nullable = false)
    private double totalCost;
    @Column(name = "date", nullable = false)
    private LocalDate date;
    @Column(name = "is_full_tank", nullable = false)
    private boolean isFullTank;

    protected Refueling(){
    }

    // Для создания нового чека в боте (генерирует новый UUID)
    public Refueling(UUID carId, int mileage, double liters, double totalCost, LocalDate date, boolean isFullTank) {
        this(UUID.randomUUID(), carId, mileage, liters, totalCost, date, isFullTank);
    }

    // Для восстановления чека из базы данных (принимает уже существующий UUID)
    public Refueling(UUID id, UUID carId, int mileage, double liters, double totalCost, LocalDate date, boolean isFullTank) {
        this.id = id;
        this.carId = carId;
        this.mileage = mileage;
        this.liters = liters;
        this.totalCost = totalCost;
        this.date = date;
        this.isFullTank = isFullTank;
    }

    public UUID getId() {
        return id;
    }
    public UUID getCarId() {
        return carId;
    }
    public int getMileage() {
        return mileage;
    }
    public double getLiters() {
        return liters;
    }
    public double getTotalCost() {
        return totalCost;
    }
    public LocalDate getDate() {
        return date;
    }
    public boolean isFullTank() {
        return isFullTank;
    }

    @Override
    public String toString() {
        String tankType = isFullTank ? "до полного 🏁" : "частичная ⛽";
        return "Заправка от " + date + ": " + liters + " л на сумму " + Math.round(totalCost) +
                " руб. (пробег: " + mileage + " км, " + tankType + ")";
    }
}
