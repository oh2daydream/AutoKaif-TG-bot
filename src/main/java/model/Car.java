package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "cars")
public class Car {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private long userId;
    @Column(name = "brand", nullable = false)
    private String brand;
    @Column(name = "model", nullable = false)
    private String model;
    @Column(name = "year", nullable = false)
    private int year;
    @Column(name = "tank_capacity", nullable = false)
    private double tankCapacity;
    @Column(name = "current_mileage", nullable = false)
    private int currentMileage;

    protected Car() {
    }

    public Car(UUID id, long userId, String brand, String model, int year, double tankCapacity, int currentMileage) {
        this.id = (id != null) ? id : UUID.randomUUID();
        this.userId = userId;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.tankCapacity = tankCapacity;
        this.currentMileage = currentMileage;
    }

    public Car(String brand, long userId, String model, int year, double tankCapacity, int currentMileage) {
        this(null, userId, brand, model, year, tankCapacity, currentMileage);
    }

    //геттеры-----------------------------------------------------------------
    public UUID getId() {
        return id;
    }

    //сеттер-----------------------------------------------------------------
    public void setId(UUID id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public double getTankCapacity() {
        return tankCapacity;
    }

    public int getCurrentMileage() {
        return currentMileage;
    }

    //методы-----------------------------------------------------------------
    public boolean updateMileage(int newMileage) {
        if (this.currentMileage <= newMileage) {
            this.currentMileage = newMileage;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Автомобиль: " + brand + " " + model + " (" + year + "г.в.)\n" +
                "• Объём бака: " + tankCapacity + "л.\n" +
                "• Текущий пробег:" + currentMileage + " км";
    }
}