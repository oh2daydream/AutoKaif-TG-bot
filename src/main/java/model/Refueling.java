package model;

import java.time.LocalDate;

public class Refueling {
    private int mileage;
    private double liters;
    private double totalCost;
    private LocalDate date;

    public Refueling(int mileage, double liters, double totalCost, LocalDate date){
        this.mileage=mileage;
        this.liters=liters;
        this.totalCost=totalCost;
        this.date=date;
    }

    public double getTotalCost() {
        return totalCost;
    }
    public LocalDate getDate() {
        return date;
    }
    public int getMileage() {
        return mileage;
    }
    public double getLiters() {
        return liters;
    }
    public String toString(){
        return "Заправка от" + date + ": " +liters+ "л. на сумму" + totalCost + "руб. (пробег " + mileage;
    }
}
