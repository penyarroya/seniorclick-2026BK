package es.jlrn.exceptions.exception;


public class UsernameAlreadyUsedException extends RuntimeException {
    public UsernameAlreadyUsedException(String username) {
        super("El nombre de usuario " + username + " ya está en uso");
    }
}
