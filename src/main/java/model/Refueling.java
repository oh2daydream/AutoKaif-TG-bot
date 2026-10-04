package model;

import java.time.LocalDate;

public class Refueling {
    private int id;
    private int carId;
    private int mileage;
    private double liters;
    private double totalCost;
    private LocalDate date;
    private boolean isFullTank;

    public Refueling(int carId,int mileage, double liters, double totalCost, LocalDate date, boolean isFullTank){
        this.carId=carId;
        this.mileage=mileage;
        this.liters=liters;
        this.totalCost=totalCost;
        this.date=date;
        this.isFullTank = isFullTank;
    }
    public int getId(){return id;}
    public void setId(int id){
        this.id=id;
    }
    public int getCarId() {return carId;}
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
    public boolean isFullTank(){return isFullTank;}
    @Override
    public String toString(){
        String tankType = isFullTank ? "до полного 🏁" : "частичная ⛽";
        return "Заправка от " + date + ": " + liters + " л на сумму " + totalCost +
                " руб. (пробег: " + mileage + " км, " + tankType + ")";
    }
}
