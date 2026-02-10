package es.jlrn.exceptions.exception;

import es.jlrn.exceptions.ValidationError;
import java.util.List;

/**
 * Excepción personalizada para manejar errores de validación de DTOs
 * o reglas de negocio en el service.
 */
public class ValidationException extends RuntimeException {

    private final List<ValidationError> errors;

    /**
     * Constructor principal que recibe la lista de errores de validación.
     * @param errors Lista de ValidationError
     */
    public ValidationException(List<ValidationError> errors) {
        super("Validation failed");
        this.errors = errors;
    }

    /**
     * Devuelve la lista de errores de validación.
     * @return List<ValidationError>
     */
    public List<ValidationError> getErrors() {
        return errors;
    }

    /**
     * Constructor opcional con mensaje y causa.
     */
    public ValidationException(String message, Throwable cause, List<ValidationError> errors) {
        super(message, cause);
        this.errors = errors;
    }
}
