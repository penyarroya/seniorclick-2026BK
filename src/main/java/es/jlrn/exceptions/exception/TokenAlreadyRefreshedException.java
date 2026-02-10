package es.jlrn.exceptions.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TokenAlreadyRefreshedException extends RuntimeException {
    
    public TokenAlreadyRefreshedException(String message) {
        super(message);
    }
}