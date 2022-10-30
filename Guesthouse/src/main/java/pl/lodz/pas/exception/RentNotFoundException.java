package pl.lodz.pas.exception;

public class RentNotFoundException extends Exception {
    public RentNotFoundException() {
    }

    public RentNotFoundException(String message) {
        super(message);
    }

    public RentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RentNotFoundException(Throwable cause) {
        super(cause);
    }
}
