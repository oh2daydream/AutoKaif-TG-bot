package task1;

public class LowerCaseHandler implements TextHandler{
    @Override
    public String handle(String text) {
        return text.toLowerCase();
    }
}