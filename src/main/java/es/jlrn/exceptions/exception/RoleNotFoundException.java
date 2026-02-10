package es.jlrn.exceptions.exception;


public class RoleNotFoundException extends RuntimeException {

    // Constructor con nombre de rol
    public RoleNotFoundException(String roleName) {
        super("Rol '" + roleName + "' no encontrado");
    }

    // Constructor con mensaje y causa (opcional)
    public RoleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

