package ru.georgeee.stardict;

public class StarDictException extends RuntimeException {
    public StarDictException() {
    }

    public StarDictException(String message) {
        super(message);
    }

    public StarDictException(String message, Throwable cause) {
        super(message, cause);
    }

    public StarDictException(Throwable cause) {
        super(cause);
    }
}
