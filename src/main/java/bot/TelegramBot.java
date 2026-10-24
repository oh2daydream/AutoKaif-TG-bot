package bot;

import handler.BotCommandHandler;
import model.Car;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class TelegramBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final BotCommandHandler botHandler;

    public TelegramBot(String botToken, String botUsername, BotCommandHandler botHandler) {
        super(botToken);
        this.botUsername = botUsername;
        this.botHandler = botHandler;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        long chatId;
        long userId;
        String userInput;

        // 1. Пользователь нажал Inline-кнопку под сообщением
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            // Извлекаем пользователя из CallbackQuery, чтобы избежать NullPointerException
            userId = update.getCallbackQuery().getFrom().getId();
            userInput = update.getCallbackQuery().getData();

            // Снимаем анимацию ожидания («часики») с кнопки
            answerCallBack(update.getCallbackQuery().getId());
        }
        // 2. Пользователь отправил текстовое сообщение или нажал кнопку нижнего меню
        else if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            userId = update.getMessage().getFrom().getId();
            userInput = update.getMessage().getText();
        }
        // 3. Другие типы апдейтов (стикеры, фото, системные уведомления) игнорируем
        else {
            return;
        }

        // Передаем команду и идентификатор пользователя в диспетчер
        String responseText = botHandler.handleMessage(userId, userInput);

        // Отправляем ответ с нужной раскладкой кнопок
        sendResponse(chatId, userId, userInput.trim(), responseText);
    }

    private void sendResponse(long chatId, long userId, String command, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        // Контекстное отображение клавиатур
        if ("/garage".equalsIgnoreCase(command)) {
            List<Car> cars = botHandler.getUserCars(userId);
            if (!cars.isEmpty()) {
                message.setReplyMarkup(KeyboardFactory.getGarageInlineKeyboard(cars, botHandler.getActiveCar(userId)));
            } else {
                message.setReplyMarkup(KeyboardFactory.getEmptyGarageKeyboard());
            }
        }
        else if (botHandler.isDialogActive(userId)) {
            message.setReplyMarkup(KeyboardFactory.getCancelKeyboard());
        }
        else if (botHandler.getActiveCar(userId) == null) {
            message.setReplyMarkup(KeyboardFactory.getEmptyGarageKeyboard());
        }
        else {
            message.setReplyMarkup(KeyboardFactory.getMainMenuKeyboard());
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("❌ Ошибка отправки сообщения в Telegram: " + e.getMessage());
        }
    }

    private void answerCallBack(String callBackId) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callBackId);
        try {
            execute(answer);
        } catch (TelegramApiException ignored) {
            // Игнорируем ошибку, если пользователь нажал кнопку несколько раз подряд
        }
    }
}