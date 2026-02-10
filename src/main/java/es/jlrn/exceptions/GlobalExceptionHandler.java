
// Cambios del dia 28/12/2025

// package es.jlrn.exceptions;

// import com.fasterxml.jackson.databind.exc.InvalidFormatException;
// import com.fasterxml.jackson.databind.exc.MismatchedInputException;
// import es.jlrn.exceptions.exception.*;
// import jakarta.persistence.EntityNotFoundException;
// import org.springframework.dao.DataIntegrityViolationException;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.http.converter.HttpMessageNotReadableException;
// import org.springframework.web.bind.MethodArgumentNotValidException;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.bind.annotation.RestControllerAdvice;

// import java.time.LocalDateTime;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;
// import java.util.Arrays;

// /**
//  * Controlador global de excepciones para toda la aplicación.
//  * 
//  * Refactorizado para mantener la lógica anterior completa y mejorar capturas de JPA/DataIntegrity.
//  */
// @RestControllerAdvice
// public class GlobalExceptionHandler {
// //
//     // ===================== EXCEPCIONES PERSONALIZADAS =====================
//     @ExceptionHandler(InvalidCredentialsException.class)
//     public ResponseEntity<Object> handleInvalidCredentials(InvalidCredentialsException ex) {
//         return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
//     }

//     @ExceptionHandler(UserNotFoundException.class)
//     public ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex) {
//         return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
//     }

//     @ExceptionHandler(EmailAlreadyUsedException.class)
//     public ResponseEntity<Object> handleEmailConflict(EmailAlreadyUsedException ex) {
//         return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
//     }

//     @ExceptionHandler(UsernameAlreadyUsedException.class)
//     public ResponseEntity<Object> handleUsernameConflict(UsernameAlreadyUsedException ex) {
//         return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
//     }

//     @ExceptionHandler(ResourceConflictException.class)
//     public ResponseEntity<Object> handleResourceConflict(ResourceConflictException ex) {
//         return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
//     }

//     @ExceptionHandler(RoleNotFoundException.class)
//     public ResponseEntity<Object> handleRoleNotFound(RoleNotFoundException ex) {
//         return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
//     }

//     @ExceptionHandler(DuplicateUserException.class)
//     public ResponseEntity<Object> handleDuplicateUser(DuplicateUserException ex) {
//         return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
//     }

//     @ExceptionHandler(EmailNotVerifiedException.class)
//     public ResponseEntity<Object> handleEmailNotVerified(EmailNotVerifiedException ex) {
//         return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
//     }

//     @ExceptionHandler(InvalidVerificationCodeException.class)
//     public ResponseEntity<Object> handleInvalidVerificationCode(InvalidVerificationCodeException ex) {
//         return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
//     }

//     @ExceptionHandler(VerificationCodeExpiredException.class)
//     public ResponseEntity<Object> handleVerificationCodeExpired(VerificationCodeExpiredException ex) {
//         return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
//     }

//     @ExceptionHandler(EmailSendingException.class)
//     public ResponseEntity<Object> handleEmailSendingError(EmailSendingException ex) {
//         return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
//     }

//     @ExceptionHandler(WeakPasswordException.class)
//     public ResponseEntity<Object> handleWeakPassword(WeakPasswordException ex) {
//         return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
//     }

//     @ExceptionHandler(InstitutionAlreadyExistsException.class)
//     public ResponseEntity<Object> handleInstitutionConflict(InstitutionAlreadyExistsException ex) {
//         return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
//     }

//     @ExceptionHandler(InstitutionNotFoundException.class)
//     public ResponseEntity<Object> handleInstitutionNotFound(InstitutionNotFoundException ex) {
//         return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
//     }

//     @ExceptionHandler(UserAlreadyExistsException.class)
//     public ResponseEntity<Object> handleUserAlreadyExists(UserAlreadyExistsException ex) {
//         return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
//     }

//     @ExceptionHandler(ResourceNotFoundException.class)
//     public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {
//         return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
//     }

//     @ExceptionHandler(EntityNotFoundException.class)
//     public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
//         return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
//     }

//     // ===================== VALIDACIONES DE DTOs =====================
//     // @ExceptionHandler(MethodArgumentNotValidException.class)
//     // public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {
//     //     Map<String, String> errors = new HashMap<>();
//     //     ex.getBindingResult().getFieldErrors()
//     //             .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

//     //     Map<String, Object> body = new HashMap<>();
//     //     body.put("timestamp", LocalDateTime.now());
//     //     body.put("status", HttpStatus.BAD_REQUEST.value());
//     //     body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
//     //     body.put("errors", errors);

//     //     return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
//     // }

//     @ExceptionHandler(MethodArgumentNotValidException.class)
//     public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
//         // 1. Transformamos los FieldErrors de Spring a tu clase ValidationError
//         List<ValidationError> validationErrors = ex.getBindingResult()
//                 .getFieldErrors()
//                 .stream()
//                 .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
//                 .collect(Collectors.toList());

//         // 2. Construimos la respuesta unificada
//         ErrorResponse errorResponse = ErrorResponse.builder()
//                 .timestamp(LocalDateTime.now())
//                 .status(HttpStatus.BAD_REQUEST.value())
//                 .error("Validation Failed")
//                 .message("Se encontraron errores en la validación de los datos")
//                 .details(validationErrors) // Aquí inyectamos tu lista
//                 .build();

//         return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//     }

//     // ===================== ERRORES DE PARSEO JSON =====================
//     @ExceptionHandler(HttpMessageNotReadableException.class)
//     public ResponseEntity<Object> handleJsonParseError(HttpMessageNotReadableException ex) {
//         String message = "Error al procesar el JSON enviado.";
//         Throwable cause = ex.getCause();

//         if (cause instanceof InvalidFormatException ife) {
//             Class<?> targetType = ife.getTargetType();
//             String fieldName = ife.getPath().stream()
//                     .map(ref -> ref.getFieldName())
//                     .reduce((first, second) -> second)
//                     .orElse("desconocido");

//             if (targetType.isEnum()) {
//                 Object[] enumValues = targetType.getEnumConstants();
//                 message = String.format(
//                         "Valor inválido para el campo '%s'. Recibido: '%s'. Valores válidos: %s",
//                         fieldName, ife.getValue(), Arrays.toString(enumValues)
//                 );
//             } else {
//                 message = String.format(
//                         "Valor inválido para el campo '%s'. Se esperaba tipo %s pero se recibió '%s'.",
//                         fieldName, targetType.getSimpleName(), ife.getValue()
//                 );
//             }
//         } else if (cause instanceof MismatchedInputException mie) {
//             String fieldName = mie.getPath().stream()
//                     .map(ref -> ref.getFieldName())
//                     .reduce((first, second) -> second)
//                     .orElse("desconocido");

//             message = String.format(
//                     "Estructura JSON inválida o campo incorrecto en '%s': %s",
//                     fieldName, mie.getOriginalMessage()
//             );
//         } else if (cause != null) {
//             message = "Error al procesar JSON: " + cause.getMessage();
//         }

//         return buildResponse(HttpStatus.BAD_REQUEST, message);
//     }

//     // ===================== CONSTRAINTS Y DATA INTEGRITY =====================
//     @ExceptionHandler(DataIntegrityViolationException.class)
//     public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
//         ex.printStackTrace();
//         String rootMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
//         return buildResponse(HttpStatus.CONFLICT, "Violación de integridad en la base de datos: " + rootMessage);
//     }

//     // ===================== TOKEN INVALIDO =====================
//     @ExceptionHandler(InvalidTokenException.class)
//     public ResponseEntity<Object> handleInvalidToken(InvalidTokenException ex) {
//         HttpStatus status = ex.isExpired() ? HttpStatus.GONE : HttpStatus.BAD_REQUEST;
//         return buildResponse(status, ex.getMessage());
//     }

//     // ===================== CAPTURA GENÉRICA =====================
//     @ExceptionHandler(Exception.class)
//     public ResponseEntity<Object> handleAll(Exception ex) {
//         ex.printStackTrace();
//         return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno: " + ex.getMessage());
//     }

//     // ===================== MÉTODO HELPER UNIFICADO =====================
//     private ResponseEntity<Object> buildResponse(HttpStatus status, String message) {
//         Map<String, Object> body = new HashMap<>();
//         body.put("timestamp", LocalDateTime.now());
//         body.put("status", status.value());
//         body.put("error", status.getReasonPhrase());
//         body.put("message", message);
//         return new ResponseEntity<>(body, status);
//     }

//     @ExceptionHandler(IllegalArgumentException.class)
//     public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
//         Map<String, Object> body = new HashMap<>();
//         body.put("timestamp", LocalDateTime.now());
//         body.put("status", HttpStatus.BAD_REQUEST.value());
//         body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
//         body.put("message", ex.getMessage());
//         return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
//     }

//     // ===================== VALIDATION EXCEPTION =====================
//     @ExceptionHandler(ValidationException.class)
//     public ResponseEntity<ErrorResponse> handleCustomValidation(ValidationException ex) {

//         ErrorResponse error = ErrorResponse.builder()
//                 .timestamp(LocalDateTime.now())
//                 .status(HttpStatus.BAD_REQUEST.value())
//                 .error("Bad Request")
//                 .message(ex.getMessage())
//                 .details(ex.getErrors())
//                 .build();

//         return ResponseEntity.badRequest().body(error);
//     }
// }




package es.jlrn.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import es.jlrn.exceptions.exception.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===================== EXCEPCIONES PERSONALIZADAS =====================

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<ErrorResponse> handleEmailConflict(EmailAlreadyUsedException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(UsernameAlreadyUsedException.class)
    public ResponseEntity<ErrorResponse> handleUsernameConflict(UsernameAlreadyUsedException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponse> handleResourceConflict(ResourceConflictException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotFound(RoleNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUser(DuplicateUserException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleEmailNotVerified(EmailNotVerifiedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidVerificationCode(InvalidVerificationCodeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(VerificationCodeExpiredException.class)
    public ResponseEntity<ErrorResponse> handleVerificationCodeExpired(VerificationCodeExpiredException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ErrorResponse> handleEmailSendingError(EmailSendingException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(WeakPasswordException.class)
    public ResponseEntity<ErrorResponse> handleWeakPassword(WeakPasswordException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InstitutionAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleInstitutionConflict(InstitutionAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InstitutionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInstitutionNotFound(InstitutionNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ===================== VALIDACIONES DE DTOs =====================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Se encontraron errores en la validación de los datos")
                .details(validationErrors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleCustomValidation(ValidationException ex) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Bad Request")
                        .message(ex.getMessage())
                        .details(ex.getErrors())
                        .build()
        );
    }

    // ===================== ERRORES DE PARSEO JSON (Lógica recuperada) =====================

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseError(HttpMessageNotReadableException ex) {
        String message = "Error al procesar el JSON enviado.";
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException ife) {
            Class<?> targetType = ife.getTargetType();
            String fieldName = ife.getPath().stream()
                    .map(ref -> ref.getFieldName())
                    .reduce((first, second) -> second)
                    .orElse("desconocido");

            if (targetType.isEnum()) {
                Object[] enumValues = targetType.getEnumConstants();
                message = String.format(
                        "Valor inválido para el campo '%s'. Recibido: '%s'. Valores válidos: %s",
                        fieldName, ife.getValue(), Arrays.toString(enumValues)
                );
            } else {
                message = String.format(
                        "Valor inválido para el campo '%s'. Se esperaba tipo %s pero se recibió '%s'.",
                        fieldName, targetType.getSimpleName(), ife.getValue()
                );
            }
        } else if (cause instanceof MismatchedInputException mie) {
            String fieldName = mie.getPath().stream()
                    .map(ref -> ref.getFieldName())
                    .reduce((first, second) -> second)
                    .orElse("desconocido");

            message = String.format(
                    "Estructura JSON inválida o campo incorrecto en '%s': %s",
                    fieldName, mie.getOriginalMessage()
            );
        }

        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    // ===================== CONSTRAINTS Y OTROS =====================

    // @ExceptionHandler(DataIntegrityViolationException.class)
    // public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
    //     ex.printStackTrace();
    //     String rootMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
    //     return buildResponse(HttpStatus.CONFLICT, "Violación de integridad: " + rootMessage);
    // }

    // @ExceptionHandler(InvalidTokenException.class)
    // public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex) {
    //     HttpStatus status = ex.isExpired() ? HttpStatus.GONE : HttpStatus.BAD_REQUEST;
    //     return buildResponse(status, ex.getMessage());
    // }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex) {
        // IMPORTANTE: Cambiamos a UNAUTHORIZED (401) para que Angular 
        // sepa que debe intentar el refresh-token.
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(TokenAlreadyRefreshedException.class)
    public ResponseEntity<ErrorResponse> handleTokenAlreadyRefreshed(TokenAlreadyRefreshedException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        ex.printStackTrace();
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno: " + ex.getMessage());
    }

    // ===================== MÉTODO HELPER UNIFICADO =====================
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .details(null) // No hay detalles de validación en errores simples
                .build();
        return new ResponseEntity<>(response, status);
    }

    // @ExceptionHandler(DataIntegrityViolationException.class)
    // public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
    //     // Extraemos el mensaje más específico (la causa raíz en la DB)
    //     String rootMessage = (ex.getRootCause() != null) ? ex.getRootCause().getMessage() : ex.getMessage();
        
    //     // Opcional: imprimir en consola para que tú como dev veas el error completo
    //     // ex.printStackTrace(); 

    //     ErrorResponse error = ErrorResponse.builder()
    //             .timestamp(LocalDateTime.now())
    //             .status(HttpStatus.CONFLICT.value())
    //             .error("Data Integrity Violation")
    //             .message("Error de consistencia en la base de datos: " + rootMessage)
    //             .details(null)
    //             .build();

    //     return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    // }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        String rootMessage = (ex.getRootCause() != null) ? ex.getRootCause().getMessage() : ex.getMessage();
        
        // Si el error es por la restricción única de inscripción
        String userMessage = "Error de consistencia en la base de datos.";
        if (rootMessage != null && rootMessage.contains("uk_user_project_enrollment")) {
            userMessage = "Ya existe una inscripción activa para este usuario en este proyecto.";
        }

        return buildResponse(HttpStatus.CONFLICT, userMessage);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        // Capturamos mensajes de lógica de negocio lanzados como RuntimeException
        if (ex.getMessage() != null && ex.getMessage().contains("ya está inscrito")) {
            return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
        }
        
        // Para cualquier otra RuntimeException, devolvemos error interno
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error de ejecución: " + ex.getMessage());
    }
    
}