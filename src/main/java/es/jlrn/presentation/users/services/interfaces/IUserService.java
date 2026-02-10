package es.jlrn.presentation.users.services.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import es.jlrn.configuration.auth.dtos.RegisterRequestDTO;
import es.jlrn.persistence.models.users.models.UserEntity;
import es.jlrn.presentation.users.dtos.Usuarios.UserCreateDTO;
import es.jlrn.presentation.users.dtos.Usuarios.UserDTO;
import es.jlrn.presentation.users.dtos.Usuarios.UserUpdateDTO;
import es.jlrn.presentation.users.dtos.Usuarios.UserVerificationDTO;

public interface IUserService extends UserDetailsService{
//
    List<UserDTO> findAll();
    public Page<UserDTO> findAll(Pageable pageable);
    UserDTO findById(Long id);  // devuelve directamente o lanza excepción

    //Page<UserDTO> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    Page<UserDTO> findByUsernameOrEmail(String search, Pageable pageable);
    Page<UserDTO> findByUsernameContaining(String search, Pageable pageable);
    
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    UserVerificationDTO verifyEmail(String email, String code);

    UserDetails loadUserByUsername(String username);

    UserDTO create(UserCreateDTO dto);
    UserDTO update(Long id, UserUpdateDTO dto);
    void deleteById(Long id);
    UserDTO createUserManual(UserCreateDTO request);
    UserVerificationDTO register(RegisterRequestDTO request);

    public UserDTO cambiarEstadoActivo(Long userId, boolean activo);
    UserDTO toggleActivo(Long id);
    void validatePassword(String password);

    UserDTO toDTO(UserEntity user);
    
    UserDTO addRoleToUser(Long userId, String roleName);
    UserDTO removeRoleFromUser(Long userId, String roleName);
}

