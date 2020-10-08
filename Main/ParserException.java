package Main;

public class ParserException extends Exception {
    public ParserException(String errorType) { //throwable error add in after
        super(errorType);
    }
}
