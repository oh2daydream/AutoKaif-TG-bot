package Bot;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
public class TelegramBot extends TelegramLongPollingBot {
    private final String botUsername;
    public TelegramBot(String botToken, String botUsername){
        super(botToken);
        this.botUsername = botUsername;
    }
    @Override
    public String getBotUsername(){
        return this.botUsername;
    }
    @Override
    public void onUpdateReceived(Update update){
        if(update.hasMessage() && update.getMessage().hasText()){
            String userText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            String reply = "Бот на связи! Вы написали: " + userText + " иди нахуй";
            sendMessage(chatId,reply);
        }
    }
    public void sendMessage(long chatId, String text){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try{
            execute(message);
        } catch (TelegramApiException e){
            System.err.println("Ошибка отправки: "+e.getMessage());
        }
    }
}
