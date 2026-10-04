package service;

import model.FuelStats;
import model.Refueling;
import repository.RefuelingRepository;
import java.util.*;
import java.time.temporal.ChronoUnit;
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
    // Возвращает все чеки по id машины для того чтобы высчитать статистику
    public List<Refueling> getAllRefuelingsByCarId(int carId){
        List<Refueling> carRecords = new ArrayList<>();
        for(Refueling r : repository.findAll()){
            if(r.getCarId() == carId){
                carRecords.add(r);
            }
        }
        return carRecords;
    }
//    общая сумма всех заправок на одной машине
    public double calculateTotalCost(int carId){
        double total = 0.0;
        for(Refueling refueling : getAllRefuelingsByCarId(carId)){
            total+=refueling.getTotalCost();
        }
        return total;
    }
    //Расчет расхода (л/100км) за все время
    public double calculateAverageConsumption(int carId){
        List<Refueling> records = getAllRefuelingsByCarId(carId);
        if (records.size() < 2)
            return 0.0;
        int firstFullIndex = -1;
        int lastFullIndex = -1;
        for(int i=0; i<records.size(); i++){
            if(records.get(i).isFullTank()){
                firstFullIndex = i;
                break;
            }
        }
        for(int i=records.size()-1; i>=0; i--){
            if(records.get(i).isFullTank()){
                lastFullIndex = i;
                break;
            }
        }
        if (firstFullIndex == -1 || lastFullIndex == -1 || firstFullIndex == lastFullIndex) {
            return 0.0;
        }
        int totalDistance = records.get(lastFullIndex).getMileage() - records.get(firstFullIndex).getMileage();
        if(totalDistance <= 0){return 0.0;}


        double totalLiters = 0.0;
        for(int i=firstFullIndex+1; i<=lastFullIndex;i++){
            totalLiters+=records.get(i).getLiters();
        }
        return (totalLiters/totalDistance)*100;
    }
    //Расчет расхода за последние две заправки
    public double calculateLastIntervalConsumption(int carId){
        List<Refueling> records = getAllRefuelingsByCarId(carId);
        if (records.size()<2){
            return 0.0;
        }
        Refueling last = records.get(records.size()-1);
        if(!last.isFullTank()){
            return  0.0;
        }
        Refueling prevFull = null;
        double accumulatedLiters = last.getLiters();
        for(int i=records.size() -2; i>=0; i--){
            Refueling current = records.get(i);
            if (current.isFullTank()){
                prevFull = current;
                break;
            } else {
                accumulatedLiters += current.getLiters();
            }
        }
        if (prevFull==null){
            return 0.0;
        }
        int distance = last.getMileage() - prevFull.getMileage();
        if(distance<=0){
            return 0.0;
        }
        return (accumulatedLiters/distance) * 100;
    }
    //Расчет сколько стоит один километр
    public double calculateLastIntervalCostPerKm(int carId){
        List<Refueling> records = getAllRefuelingsByCarId(carId);
        if(records.size()<2){
            return 0.0;
        }
        Refueling last = records.get(records.size()-1);
        if(!last.isFullTank()){return 0.0;}
        Refueling prevFull = null;
        double accumulatedCost= last.getTotalCost();
        for(int i=records.size()-2; i>=0;i--){
            Refueling current = records.get(i);
            if(current.isFullTank()){
                prevFull = current;
                break;
            } else {
                accumulatedCost += current.getTotalCost();
            }
        }
        if(prevFull == null){
            return 0.0;
        }
        int distance = last.getMileage()-prevFull.getMileage();
        if(distance<=0){
            return 0.0;
        }
        return accumulatedCost / distance;
    }
    public FuelStats calculacteFuelStats(int carId){
        List<Refueling> records = getAllRefuelingsByCarId(carId);
        if(records.isEmpty()){return null;}
        FuelStats stats = new FuelStats();
        stats.totalRefuelings = records.size();

        int totalDistance = 0;
        long totalDaysBetween = 0;
        int intervalsCount = 0;

        for (int i = 0; i < records.size(); i++) {
            Refueling current = records.get(i);

            if (current.isFullTank()) {
                stats.fullTankCount++;
            } else {
                stats.partialTankCount++;
            }

            stats.totalLiters += current.getLiters();
            stats.totalCost += current.getTotalCost();

            //Интервалы между соседними заправками
            if (i > 0) {
                Refueling prev = records.get(i - 1);
                int deltaKm = current.getMileage() - prev.getMileage();
                long deltaDays = ChronoUnit.DAYS.between(prev.getDate(), current.getDate());

                if (deltaKm > 0) {
                    intervalsCount++;
                    totalDistance += deltaKm;
                    stats.lastDistance = deltaKm;
                }

                if (deltaDays >= 0) {
                    totalDaysBetween += deltaDays;
                    stats.lastDaysBetweenRefuels = deltaDays;
                }
            }
        }

        //Показатели крайнего чека
        Refueling lastRecord = records.get(records.size() - 1);
        stats.lastLiters = lastRecord.getLiters();
        stats.lastCost = lastRecord.getTotalCost();
        if (stats.lastLiters > 0) {
            stats.lastPricePerLiter = stats.lastCost / stats.lastLiters;
        }

        //Средние значения объёмов, чеков и цен
        stats.avgLiters = stats.totalLiters / stats.totalRefuelings;
        stats.avgCost = stats.totalCost / stats.totalRefuelings;
        if (stats.totalLiters > 0) {
            stats.avgPricePerLiter = stats.totalCost / stats.totalLiters;
        }

        //Средние дистанции и дни между АЗС
        if (intervalsCount > 0) {
            stats.avgDistance = (double) totalDistance / intervalsCount;
            stats.avgDaysBetweenRefuels = (double) totalDaysBetween / intervalsCount;
        }

        //Расход в день (л/день)
        long totalPeriodDays = ChronoUnit.DAYS.between(records.get(0).getDate(), lastRecord.getDate());
        if (totalPeriodDays > 0) {
            stats.avgLitersPerDay = stats.totalLiters / totalPeriodDays;
        }
        if (stats.lastDaysBetweenRefuels > 0) {
            stats.lastLitersPerDay = stats.lastLiters / stats.lastDaysBetweenRefuels;
        }

        //Расход топлива
        stats.avgConsumption = calculateAverageConsumption(carId);
        stats.lastConsumption = calculateLastIntervalConsumption(carId);

        //Эффективность (км на 1 литр)
        if (stats.avgConsumption > 0) {
            stats.avgKmPerLiter = 100.0 / stats.avgConsumption;
        }
        if (stats.lastConsumption > 0) {
            stats.lastKmPerLiter = 100.0 / stats.lastConsumption;
        }

        return stats;
    }

}
