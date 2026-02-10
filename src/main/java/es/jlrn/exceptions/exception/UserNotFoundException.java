package es.jlrn.exceptions.exception;


public class UserNotFoundException extends RuntimeException {

    // Constructor con ID
    public UserNotFoundException(Long id) {
        super("Usuario con id " + id + " no encontrado");
    }

    // Constructor con mensaje personalizado
    public UserNotFoundException(String message) {
        super(message);
    }

    // Constructor con mensaje y causa
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
