package model;

/**
 * Объект-контейнер со статистикой (только средние и последние показатели).
 */
public class FuelStats {

    // 1. Счётчики чеков
    public int totalRefuelings;
    public int fullTankCount;
    public int partialTankCount;

    // 2. Расход топлива (л/100 км)
    public double avgConsumption;
    public double lastConsumption;

    // 3. Эффективность (км/л)
    public double avgKmPerLiter;
    public double lastKmPerLiter;

    // 4. Расход топлива в день (л/день)
    public double avgLitersPerDay;
    public double lastLitersPerDay;

    // 5. Цена за литр (₽/л)
    public double avgPricePerLiter;
    public double lastPricePerLiter;

    // 6. Периодичность заправок (дней между АЗС)
    public double avgDaysBetweenRefuels;
    public long lastDaysBetweenRefuels;

    // 7. Пробег на баке (км между заправками)
    public double avgDistance;
    public int lastDistance;

    // 8. Объём топлива (л)
    public double totalLiters;
    public double avgLiters;
    public double lastLiters;

    // 9. Финансы (₽)
    public double totalCost;
    public double avgCost;
    public double lastCost;
}