package es.jlrn.exceptions.exception;


public class InvalidVerificationCodeException extends RuntimeException {
//
    public InvalidVerificationCodeException() {
        super("Código de verificación inválido");
    }

    public InvalidVerificationCodeException(String message) {
        super(message);
    }
}
