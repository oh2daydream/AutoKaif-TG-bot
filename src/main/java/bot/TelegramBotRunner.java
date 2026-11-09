package bot;

import handler.BotCommandHandler;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TelegramBotRunner implements Bot{
    private final BotCommandHandler botHandler;

    public TelegramBotRunner(BotCommandHandler botHandler){
        this.botHandler = botHandler;
    }
    @Override
    public void run(){
        String botToken = System.getenv("BOT_TOKEN");
        String botUsername = System.getenv("BOT_USERNAME");

        if(botToken == null || botToken.isBlank()){
            System.err.println("❌ Ошибка: не задана переменная BOT_TOKEN!");
            return;
        }
        try{
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            TelegramBot bot = new TelegramBot(botToken, botUsername, botHandler);
            botsApi.registerBot(bot);
            System.out.println("🚀 Telegram-бот AutoKaif успешно запущен в сети!");
        } catch (TelegramApiException e){
            System.err.println("❌ Ошибка при запуске Telegram-бота: " + e.getMessage());
        }
    }
}
