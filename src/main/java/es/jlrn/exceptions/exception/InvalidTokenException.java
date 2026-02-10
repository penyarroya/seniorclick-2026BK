// package es.jlrn.exceptions.exception;

// public class InvalidTokenException extends RuntimeException {
//     /**
//      * Lanza la excepción con un mensaje de error específico.
//      * Ejemplo: "El token ha expirado. Por favor, solicita un nuevo enlace."
//      */
//     public InvalidTokenException(String message) {
//         super(message);
//     }
// }

package es.jlrn.exceptions.exception;

public class InvalidTokenException extends RuntimeException {

    private final boolean expired;

    /**
     * Constructor principal
     * @param message Mensaje de error
     * @param expired true si el token ha expirado, false si es inválido
     */
    public InvalidTokenException(String message, boolean expired) {
        super(message);
        this.expired = expired;
    }

    /**
     * Constructor por defecto (no expirado)
     * @param message Mensaje de error
     */
    public InvalidTokenException(String message) {
        this(message, false);
    }

    /**
     * Indica si el token estaba expirado
     * @return true si expirado, false si inválido
     */
    public boolean isExpired() {
        return expired;
    }
}
