package es.jlrn.persistence.enums;

public enum VerificationStatus {
    SUCCESS,       // Código correcto y verificado
    PENDING,       // Verificación pendiente
    INCORRECT,     // Código incorrecto
    EXPIRED,       // Código expirado
    NOT_FOUND      // Usuario no encontrado
}

