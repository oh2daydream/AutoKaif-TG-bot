package model;

import java.util.UUID;

public class Car {
    private UUID id;
    private final long userId;
    private final String brand;
    private final String model;
    private final int year;
    private final double tankCapacity;
    private int currentMileage;

    public Car(UUID id,long userId, String brand, String model, int year, double tankCapacity, int currentMileage) {
        this.id = id;
        this.userId =  userId;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.tankCapacity = tankCapacity;
        this.currentMileage = currentMileage;
    }

    public Car(String brand,long userId, String model, int year, double tankCapacity, int currentMileage) {
        this(null,userId, brand, model, year, tankCapacity, currentMileage);
    }

    //геттеры-----------------------------------------------------------------
    public UUID getId() {
        return id;
    }

    public long getUserId(){return userId;}

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

    //сеттер-----------------------------------------------------------------
    public void setId(UUID id) {
        this.id = id;
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