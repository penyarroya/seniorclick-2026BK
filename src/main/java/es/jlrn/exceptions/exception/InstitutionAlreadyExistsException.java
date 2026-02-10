package es.jlrn.exceptions.exception;

public class InstitutionAlreadyExistsException extends RuntimeException {
    public InstitutionAlreadyExistsException(String message) {
        super(message);
    }
}