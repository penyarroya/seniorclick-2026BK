package es.jlrn.exceptions.exception;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String email) {
        super("El correo electrónico " + email + " ya está registrado");
    }
}
