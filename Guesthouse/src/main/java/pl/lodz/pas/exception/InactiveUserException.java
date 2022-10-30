package pl.lodz.pas.exception;

public class InactiveUserException extends Exception {
    public InactiveUserException() {
    }

    public InactiveUserException(String message) {
        super(message);
    }

    public InactiveUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public InactiveUserException(Throwable cause) {
        super(cause);
    }
}
