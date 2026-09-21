package task1;
import java.util.Scanner;
public class Main {
    public static void main(String[] args){
        TextHandler handler = new CamelCaseHandler();
        Scanner scan= new Scanner(System.in);
        while (true){
            String input = scan.nextLine();

            if("exit".equalsIgnoreCase(input.trim())){
                System.out.println("Stop");
                break;
            }
            if(input.trim().isEmpty()){
                System.out.println("введите что-нибудь");
                continue;
            }
            String output=handler.handle(input);
            System.out.println("РЕзультат: " + output);
        }
    }

}
