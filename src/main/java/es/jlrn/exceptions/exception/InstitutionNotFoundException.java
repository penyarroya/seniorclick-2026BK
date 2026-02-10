package es.jlrn.exceptions.exception;

public class InstitutionNotFoundException extends RuntimeException {
//    
    public InstitutionNotFoundException(String message) {
        super(message);
    }

    public InstitutionNotFoundException(Long id) {
        super("Institution not found with id " + id);
    }
}