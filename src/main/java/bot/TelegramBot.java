package bot;

import handler.BotCommandHandler;
import model.Car;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class TelegramBot extends TelegramLongPollingBot{
    private final String botUsername;
    private final BotCommandHandler botHandler;

    public TelegramBot(String botToken, String botUsername, BotCommandHandler botHandler){
        super(botToken);
        this.botUsername = botUsername;
        this.botHandler = botHandler;
    }
    @Override
    public String getBotUsername(){
        return botUsername;
    }
    @Override
    public void onUpdateReceived(Update update){
        long chatId;
        String userInput;

        if(update.hasCallbackQuery()){
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userInput = update.getCallbackQuery().getData();
            String callbackId= update.getCallbackQuery().getId();
        }
        else if(update.hasMessage() && update.getMessage().hasText()){
            chatId = update.getMessage().getChatId();
            userInput = update.getMessage().getText();
        } else {
            return;
        }

        String responseText = botHandler.handleMessage(userInput);

        sendResponse(chatId, userInput.trim(), responseText);
    }

    private void sendResponse(long chatId, String command, String text){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        if(botHandler.isDialogActive()){
            message.setReplyMarkup(KeyboardFactory.getCancelKeyboard());
        }
        else if(botHandler.getActiveCar()==null){
            message.setReplyMarkup(KeyboardFactory.getStartMenuKeyboard());
            KeyboardFactory.removeKeyboard();
        }
        else {
            message.setReplyMarkup(KeyboardFactory.getMainMenuKeyboard());
        }
        try{
            execute(message);
        } catch (TelegramApiException e){
            System.err.println("❌ Ошибка отправки сообщения в Telegram: " + e.getMessage());
        }
    }
    private void answerCallBack(String callBackId){
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callBackId);
        try{
            execute(answer);
        } catch (TelegramApiException ignored){}

    }
}
