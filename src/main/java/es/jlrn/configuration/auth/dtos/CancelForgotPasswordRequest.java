package es.jlrn.configuration.auth.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelForgotPasswordRequest {
    private String email;

    public CancelForgotPasswordRequest() {} // constructor vacío necesario para Jackson
}
