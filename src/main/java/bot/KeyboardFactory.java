package bot;

import model.Car;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.List;
import java.util.ArrayList;

public class KeyboardFactory {
    public static InlineKeyboardMarkup getGarageInlineKeyboard(List<Car> cars, Car activeCar){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for(int i = 0; i < cars.size();i++){
            Car car = cars.get(i);
            InlineKeyboardButton button = new InlineKeyboardButton();

            boolean isCurrent = activeCar!=null && activeCar.getId().equals(car.getId());
            String prefix = isCurrent ? "⭐️ " : "🚗 ";

            button.setText(prefix + (i+1) + "." + car.getBrand()+ " " + car.getModel());
            button.setCallbackData("/select "+(i+1));

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
    public static ReplyKeyboardMarkup getStartMenuKeyboard(){
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
    public static ReplyKeyboardMarkup getMainMenuKeyboard(){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("/refuel"));
        row1.add(new KeyboardButton("/stats"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("/garage"));
        row2.add(new KeyboardButton("/car"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("/addcar"));
        row3.add(new KeyboardButton("/history"));
        row3.add(new KeyboardButton("/help"));

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
    public static ReplyKeyboardMarkup getCancelKeyboard(){
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
    public static ReplyKeyboardRemove removeKeyboard(){
        ReplyKeyboardRemove removeMarkup = new ReplyKeyboardRemove();
        removeMarkup.setRemoveKeyboard(true);
        return removeMarkup;
    }
}
