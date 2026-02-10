package es.jlrn.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor // Importante para que Jackson pueda deserializar si es necesario
public class ErrorResponse {
//
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;

    // Puede contener errores de validación u otros detalles
    //private List<ValidationError> details;
    private Object details;
}
