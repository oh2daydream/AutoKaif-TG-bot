package bot;

import model.Car;
import org.glassfish.jersey.message.internal.InboundMessageContext;
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
    public static InlineKeyboardMarkup getEmptyGarageKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton btn1 = new InlineKeyboardButton();
        btn1.setText("Добавить машину");
        btn1.setCallbackData("/addcar");
        row1.add(btn1);
        buttons.add(row1);
        keyboardMarkup.setKeyboard(buttons);
        return keyboardMarkup;
    }
    public static ReplyKeyboardMarkup getStartMenuInlineButtons(){
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
    public static InlineKeyboardMarkup getMainMenuInLineKeyboard(){
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton btn1 = new InlineKeyboardButton();
        btn1.setText("⛽ Добавить заправку");
        btn1.setCallbackData("/refuel");
        row1.add(btn1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton btn2 = new InlineKeyboardButton();
        btn2.setText("Машина");
        btn2.setCallbackData("/car");
        row2.add(btn2);

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        InlineKeyboardButton btn3 = new InlineKeyboardButton();
        btn3.setText("Гараж");
        btn3.setCallbackData("/garage");
        row3.add(btn3);

        List<InlineKeyboardButton> row4 = new ArrayList<>();
        InlineKeyboardButton btn4 = new InlineKeyboardButton();
        btn4.setText("Статистика заправок");
        btn4.setCallbackData("/stats");
        InlineKeyboardButton btn5 = new InlineKeyboardButton();
        btn5.setText("История заправок");
        btn5.setCallbackData("/history");
        row4.add(btn4);
        row4.add(btn5);

        buttons.add(row1);
        buttons.add(row2);
        buttons.add(row3);
        buttons.add(row4);

        keyboardMarkup.setKeyboard(buttons);
        return keyboardMarkup;
    }
    public static InlineKeyboardMarkup getCancelKeyboard(){
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> button = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText("Отмена");
        btn.setCallbackData("/cancel");
        row.add(btn);
        button.add(row);

        keyboardMarkup.setKeyboard(button);
        return keyboardMarkup;
    }
    public static ReplyKeyboardRemove removeKeyboard(){
        ReplyKeyboardRemove removeMarkup = new ReplyKeyboardRemove();
        removeMarkup.setRemoveKeyboard(true);
        return removeMarkup;
    }
}
