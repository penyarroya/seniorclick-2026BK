package es.jlrn.configuration.auth.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginEmailRequestDTO {
    private String email;
    private String password;
}