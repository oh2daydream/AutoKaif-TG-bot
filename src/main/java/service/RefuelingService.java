package service;

import model.Refueling;

import java.util.ArrayList;
import java.util.List;

public class RefuelingService {

    private final List<Refueling> refuelings = new ArrayList<>();
    public void addRefueling(Refueling refueling){
        if(refueling!=null){
            refuelings.add(refueling);
        }
    }
    public List<Refueling> getAllRefuelings(){
        return new ArrayList<>(refuelings);
    }
    public double calculateTotalCost(){
        double total = 0.0;
        for(Refueling refueling : refuelings){
            total+=refueling.getTotalCost();
        }
        return total;
    }
    public double calculateAverageConsumption(){
        if (refuelings.size() < 2)
            return 0.0;
        Refueling firstRefueling = refuelings.getFirst();
        Refueling lastRefueling = refuelings.getLast();

        int totalDistance = lastRefueling.getMileage()-firstRefueling.getMileage();

        if(totalDistance <= 0)
            return 0.0;

        double totalLiters = 0.0;
        for(int i=1; i<refuelings.size();i++){
            totalLiters+=refuelings.get(i).getLiters();
        }
        return (totalLiters/totalDistance)*100;
    }
    public double calculateLastIntervalConsumption(){
        if (refuelings.size()<2){
            return 0.0;
        }
        int distance = refuelings.getLast().getMileage()-refuelings.get(refuelings.size()-1).getMileage();
        if(distance <=0 ) return 0.0;
        return (refuelings.getLast().getLiters()/distance) * 100;
    }
}
