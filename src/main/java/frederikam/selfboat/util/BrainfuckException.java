package frederikam.selfboat.util;

public class BrainfuckException extends RuntimeException {

    public BrainfuckException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public BrainfuckException(String string) {
        super(string);
    }

}
