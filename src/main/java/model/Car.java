package model;

public class Car {
    private int id;
    private final String brand;
    private final String model;
    private final int year;
    private final double tankCapacity;
    private int currentMileage;

    public Car(int id, String brand, String model, int year, double tankCapacity, int currentMileage) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.tankCapacity = tankCapacity;
        this.currentMileage = currentMileage;
    }
    public Car(String brand, String model, int year, double tankCapacity, int currentMileage) {
        this(0, brand, model, year, tankCapacity ,currentMileage);
    }
    //геттеры-----------------------------------------------------------------
    public int getId() {
        return id;
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
    //сеттер-----------------------------------------------------------------
    public void setId(int id) {
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