package task1;

import java.util.Locale;

public class CamelCaseHandler implements TextHandler {
    @Override
    public String handle(String text) {
        String[] Words = text.trim().split(" ");
        StringBuilder result = new StringBuilder();

        result.append(Words[0].toLowerCase());

        for(int i=1;i<Words.length; i++){
            String Word = Words[i];
            if(!Word.isEmpty()){
                result.append(Character.toUpperCase(Word.charAt(0)));
                if(Word.length()>1){
                    result.append(Word.substring(1).toLowerCase());
                }
            }
        }
        return result.toString();
    }
}
