package handler;

import service.RefuelingService;
import model.Refueling;
import service.RefuelingService;
import java.time.LocalDate;
import java.util.List;
public class BotCommandHandler {
    private final RefuelingService refuelingService;

    public BotCommandHandler(RefuelingService refuelingService) {
        this.refuelingService = refuelingService;
    }

    private enum State{
        IDLE,
        WAITING_FOR_REFUEL_DATA ,
        WAITING_FOR_CAR_DATA
    }
    private State currentState = State.IDLE;
    public String handleMessage(String input){
        if (input == null || input.trim().isEmpty()){
            return "Пожайлуста, Введите команду или текст";
        }
        String message = input.trim();

        if(currentState == State.IDLE){
            return processComand(message);
        }

        if(currentState == State.WAITING_FOR_REFUELING){
            return processRefuelingInput(message);
        }
        return "Неисвезстное состояние системы";
    }
    private String processComand(String commamd){
        switch (commamd){
            case "/start":
                return "👋 Привет! Я бот бортового журнала AutoKaif.\n\n" +
                        "Доступные команды:\n" +
                        "/refuel - Записать новую заправку\n" +
                        "/stats - Посмотреть статистику расхода и затрат\n" +
                        "/history - История всех заправок\n" +
                        "/help - Справка";
            case "/help":
                return "ℹ️ Справка:\n" +
                        "• Для расчёта среднего расхода нужно внести минимум 2 заправки.\n" +
                        "• Команда /refuel запускает пошаговое добавление записи.\n" +
                        "• Для выхода из консоли введите 'exit'.";
            case "/refue"
        }
    }

}
