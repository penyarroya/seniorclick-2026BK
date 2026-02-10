package es.jlrn.exceptions.exception;

public class DataIntegrityViolationException extends RuntimeException {
    public static final long serialVersionUID = 1L;
    public DataIntegrityViolationException(String message) {
        super(message);
    }
}