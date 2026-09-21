package task1;

public class UpperCaseHandler implements TextHandler{
    @Override
    public String handle(String text) {
        return text.toUpperCase();
    }
}