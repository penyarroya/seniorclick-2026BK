package es.jlrn.exceptions.exception;

public class DuplicateUserException extends RuntimeException {
//
    public DuplicateUserException(String usernameOrEmail) {
        super("El usuario o email '" + usernameOrEmail + "' ya existe");
    }

    public DuplicateUserException(String message, Throwable cause) {
        super(message, cause);
    }
}