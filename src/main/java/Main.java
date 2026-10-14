import Bot.TelegramBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
public class Main {
    public static void main(String[] args){
        String botToken = System.getenv("BOT_TOKEN");
        String botUsername = System.getenv("BOT_USERNAME");

        if(botToken == null || botToken.isBlank() ||botUsername==null || botUsername.isBlank()){
            System.err.println("❌ Ошибка запуска: переменные окружения BOT_TOKEN и BOT_USERNAME должны быть заданы!");
            System.err.println("👉 В IntelliJ IDEA: Run -> Edit Configurations... -> Environment variables");
            return;
        }
        System.out.println("🔧 Инициализация Telegram-бота: @" + botUsername + "...");
        try{
            TelegramBot bot = new TelegramBot(botToken, botUsername);

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            botsApi.registerBot(bot);

            System.out.println("🚀 Бот успешно подключился к серверам Telegram и ожидает сообщений!");
            System.out.println("💡 Откройте диалог с ботом в Telegram и отправьте любое тестовое сообщение.");
        } catch (TelegramApiException e){
            System.err.println("❌ Ошибка при регистрации бота в Telegram API:");
            e.printStackTrace();
        }
    }
}
