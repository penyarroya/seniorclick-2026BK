package es.jlrn.configuration.auth.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForgotPasswordResponseDTO {
    private boolean success;
    private String message;
}
