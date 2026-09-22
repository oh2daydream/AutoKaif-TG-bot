package service;

import model.Refueling;
import repository.RefuelingRepository;
import java.util.ArrayList;
import java.util.List;

public class RefuelingService {

    private final RefuelingRepository repository;

    public RefuelingService(RefuelingRepository repository) {
        this.repository = repository;
    }

    public void addRefueling(Refueling refueling){
        if(refueling!=null){
            repository.save(refueling);
        }
    }
    public List<Refueling> getAllRefuelings(){
        return repository.findAll();
    }
    public double calculateTotalCost(){
        double total = 0.0;
        for(Refueling refueling : repository.findAll()){
            total+=refueling.getTotalCost();
        }
        return total;
    }
    public double calculateAverageConsumption(){
        List<Refueling> records = repository.findAll();
        if (records.size() < 2)
            return 0.0;
        Refueling firstRefueling = records.getFirst();
        Refueling lastRefueling = records.getLast();

        int totalDistance = lastRefueling.getMileage()-firstRefueling.getMileage();

        if(totalDistance <= 0)
            return 0.0;

        double totalLiters = 0.0;
        for(int i=1; i<records.size();i++){
            totalLiters+=records.get(i).getLiters();
        }
        return (totalLiters/totalDistance)*100;
    }
    public double calculateLastIntervalConsumption(){
        List<Refueling> records = repository.findAll()
        if (records.size()<2){
            return 0.0;
        }
        int distance = records.getLast().getMileage()-records.get(records.size()-1).getMileage();
        if(distance <=0 ) return 0.0;
        return (records.getLast().getLiters()/distance) * 100;
    }
    public double calculateKilometrPerRuble(){
        List<Refueling> records = repository.findAll();
        if(records.size()<2){
            return 0.0;
        }
        int totalSum = 0;
        for(Refueling refueling : repository.findAll()){
            totalSum+=refueling.getTotalCost();
        }
        int distance = records.getLast().getMileage()-records.getFirst().getMileage();
        if(distance<=0) return 0.0;
        return (totalSum/distance);
    }
}
