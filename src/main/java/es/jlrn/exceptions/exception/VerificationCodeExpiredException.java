package es.jlrn.exceptions.exception;

public class VerificationCodeExpiredException extends RuntimeException {
//    
    public VerificationCodeExpiredException() {
        super("El código de verificación ha expirado");
    }
}
