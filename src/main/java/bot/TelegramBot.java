package bot;

import handler.BotCommandHandler;
import model.Car;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class TelegramBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final BotCommandHandler botHandler;

    public TelegramBot(String botToken, String botUsername, BotCommandHandler botHandler) {
        super(botToken);
        this.botUsername = botUsername;
        this.botHandler = botHandler;
        registerBotCommands();
    }
    private void registerBotCommands(){
        BotCommand command = new BotCommand("/start", "Главное меню");
        BotCommand command1 = new BotCommand("/help", "Помощь. Список Доступных команд");
        try{
            execute(new SetMyCommands(List.of(command, command1), new BotCommandScopeDefault(),null));
        } catch (TelegramApiException e){
            System.err.println("⚠️ Не удалось настроить меню команд: " + e.getMessage());
        }
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

        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userId = update.getCallbackQuery().getFrom().getId();
            userInput = update.getCallbackQuery().getData();

            answerCallBack(update.getCallbackQuery().getId());
        }
        else if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            userId = update.getMessage().getFrom().getId();
            userInput = update.getMessage().getText();
        }
        else {
            return;
        }

        String responseText = botHandler.handleMessage(userId, userInput);

        sendResponse(chatId, userId, userInput.trim(), responseText);
    }

    private void sendResponse(long chatId, long userId, String command, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        if(botHandler. isDialogActive(userId)){
            message.setReplyMarkup(KeyboardFactory.getCancelKeyboard());
        }
        else if(botHandler.getActiveCar(userId)==null){
            message.setReplyMarkup(KeyboardFactory.getEmptyGarageKeyboard());
        }
        else if ("/garage".equalsIgnoreCase(command)) {
            List<Car> cars = botHandler.getUserCars(userId);
            if (!cars.isEmpty()) {
                message.setReplyMarkup(KeyboardFactory.getGarageInlineKeyboard(cars, botHandler.getActiveCar(userId)));
            } else {
                message.setReplyMarkup(KeyboardFactory.getEmptyGarageKeyboard());
            }
        }
        else {
            message.setReplyMarkup(KeyboardFactory.getMainMenuInLineKeyboard());
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
        }
    }
}