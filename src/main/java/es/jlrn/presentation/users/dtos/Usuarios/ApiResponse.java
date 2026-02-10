package es.jlrn.presentation.users.dtos.Usuarios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ApiResponse<T> {
//    
    private String status;
    private String message;
    private T data;
}
