package bot;

import model.Car;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardFactory {
    public static InlineKeyboardMarkup getGarageInlineKeyboard(List<Car> cars, Car activeCar) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        InlineKeyboardButton buttonAddCar = new InlineKeyboardButton();
        buttonAddCar.setText("Добавить машину");
        buttonAddCar.setCallbackData("/addcar");
        rows.add(List.of(buttonAddCar));
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            InlineKeyboardButton button = new InlineKeyboardButton();

            boolean isCurrent = activeCar != null && activeCar.getId().equals(car.getId());
            String prefix = isCurrent ? "⭐️ " : "🚗 ";

            button.setText(prefix + (i + 1) + "." + car.getBrand() + " " + car.getModel());
            button.setCallbackData("/select " + (i + 1));

            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            rows.add(row);
        }
        markup.setKeyboard(rows);
        return markup;
    }

    // Меню для пользователя с пустым гаражом
    public static ReplyKeyboardMarkup getEmptyGarageKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true); // Подгоняет размер кнопок под экран
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        // Добавляем только две актуальные команды
        row.add(new KeyboardButton("/addcar"));
        row.add(new KeyboardButton("/help"));

        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup getStartMenuKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("/addcar"));
        row.add(new KeyboardButton("/help"));
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup getMainMenuKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton refuelBtn = new InlineKeyboardButton();
        refuelBtn.setText("⛽ Добавить заправку");
        refuelBtn.setCallbackData("/refuel");
        row1.add(refuelBtn);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton carBtn = new InlineKeyboardButton();
        carBtn.setText("Машина");
        carBtn.setCallbackData("/car");
        row2.add(carBtn);

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        InlineKeyboardButton garageBtn = new InlineKeyboardButton();
        garageBtn.setText("Гараж");
        garageBtn.setCallbackData("/garage");
        row3.add(garageBtn);

        List<InlineKeyboardButton> row4 = new ArrayList<>();
        InlineKeyboardButton statsBtn = new InlineKeyboardButton();
        statsBtn.setText("Статистика заправок");
        statsBtn.setCallbackData("/stats");

        InlineKeyboardButton historyBtn = new InlineKeyboardButton();
        historyBtn.setText("История заправок");
        historyBtn.setCallbackData("/history");

        row4.add(statsBtn);
        row4.add(historyBtn);

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup getCancelKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("/cancel"));
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public static ReplyKeyboardRemove removeKeyboard() {
        ReplyKeyboardRemove removeMarkup = new ReplyKeyboardRemove();
        removeMarkup.setRemoveKeyboard(true);
        return removeMarkup;
    }
}
