package es.jlrn.presentation.users.controllers;

import es.jlrn.configuration.auth.dtos.RegisterRequestDTO;
import es.jlrn.presentation.users.dtos.Usuarios.*;
import es.jlrn.presentation.users.services.interfaces.IUserService;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    // =====================================
    // 🔹 REGISTRO
    // =====================================
    @PostMapping("/register")
    public ResponseEntity<UserVerificationDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(userService.register(request));
    }

    // =====================================
    // 🔹 VERIFICAR EMAIL
    // =====================================
    @PostMapping("/verify")
    public ResponseEntity<UserVerificationDTO> verifyEmail(
            @RequestParam String email,
            @RequestParam String code) {

        UserVerificationDTO result = userService.verifyEmail(email, code);

        return switch (result.getStatus()) {
            case NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            case INCORRECT -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            case EXPIRED -> ResponseEntity.status(HttpStatus.GONE).body(result);
            default -> ResponseEntity.ok(result);
        };
    }

    // =====================================
    // 🔹 LISTAR TODOS
    // =====================================
    @GetMapping
    public ResponseEntity<List<UserDTO>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    // =====================================
    // 🔹 LISTAR POR PÁGINAS
    // =====================================
    @GetMapping("/page")
    public ResponseEntity<Page<UserDTO>> findAllPage(
            Pageable pageable,
            @RequestParam Map<String, String> allParams) { // Captura TODOS los parámetros dinámicamente

        String global = allParams.get("global");
        
        if (global != null && !global.isBlank()) {
            return ResponseEntity.ok(userService.findByUsernameOrEmail(global, pageable));
        }
        
        // Aquí podrías añadir lógica para los otros filtros si los necesitas en el futuro
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    // =====================================
    // 🔹 BUSCAR por nombre o email
    // =====================================
    @GetMapping("/search/nameoremail")
    public ResponseEntity<Page<UserDTO>> search(@RequestParam String value, Pageable pageable) {
        return ResponseEntity.ok(userService.findByUsernameOrEmail(value, pageable));
    }

    // =====================================
    // 🔹 OBTENER POR ID
    // =====================================
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    // =====================================
    // 🔹 CREAR USUARIO
    // =====================================
    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserCreateDTO dto) {
        UserDTO createdUser = userService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // =====================================
    // 🔹 ACTUALIZAR USUARIO
    // =====================================
    // @PutMapping("/{id}")
    // public ResponseEntity<UserDTO> update(
    //         @PathVariable Long id,
    //         @Valid @RequestBody UserUpdateDTO dto) {

    //     // Validación mínima: al menos un campo debe estar presente
    //     if (dto.getUsername() == null && dto.getEmail() == null &&
    //         dto.getPassword() == null && dto.getRoles() == null && dto.getActivo() == null) {
    //         return ResponseEntity.badRequest().body(null);
    //     }

    //     UserDTO updatedUser = userService.update(id, dto);
    //     return ResponseEntity.ok(updatedUser);
    // }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {

        // 🔹 ACTUALIZADO: Incluimos los campos de perfil en la validación mínima
        if (dto.getUsername() == null && dto.getEmail() == null &&
            dto.getPassword() == null && dto.getRoles() == null && 
            dto.getActivo() == null && dto.getFirstName() == null && 
            dto.getLastName() == null && dto.getPhone() == null) {
            
            return ResponseEntity.badRequest().build();
        }

        UserDTO updatedUser = userService.update(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    // =====================================
    // 🔹 BORRAR USUARIO
    // =====================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // =====================================
    // 🔹 VALIDACIONES EXISTENCIA
    // =====================================
    @GetMapping("/exists/username")
    public ResponseEntity<Boolean> existsByUsername(@RequestParam String username) {
        return ResponseEntity.ok(userService.existsByUsername(username));
    }

    @GetMapping("/exists/email")
    public ResponseEntity<Boolean> existsByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.existsByEmail(email));
    }

    // =====================================
    // 🔹 Toggle activo
    // =====================================
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<UserDTO> toggleActivo(@PathVariable Long id) {
        return ResponseEntity.ok(userService.toggleActivo(id));
    }

    // =====================================
    // 🔹 Cambiar estado activo
    // =====================================
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserDTO> cambiarEstadoActivo(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> status) {

        boolean activo = status.getOrDefault("activo", false);
        return ResponseEntity.ok(userService.cambiarEstadoActivo(id, activo));
    }

    // =====================================
    // 🔹 Roles
    // =====================================
    @PatchMapping("/{id}/roles/add")
    public ResponseEntity<UserDTO> addRoleToUser(@PathVariable Long id, @RequestParam String roleName) {
        return ResponseEntity.ok(userService.addRoleToUser(id, roleName));
    }

    @PatchMapping("/{id}/roles/remove")
    public ResponseEntity<UserDTO> removeRoleFromUser(@PathVariable Long id, @RequestParam String roleName) {
        return ResponseEntity.ok(userService.removeRoleFromUser(id, roleName));
    }

    // =====================================
    // 🔹 Crear usuario manual
    // =====================================
    // @PostMapping("/manual")
    // public ResponseEntity<UserDTO> createManual(@Valid @RequestBody UserCreateDTO dto) {
    //     UserEntity user = userService.createUserManual(dto);
    //     return ResponseEntity.ok(userService.toDTO(user));
    // }

    // =====================================
    // 🔹 Crear usuario manual (ADMIN)
    // =====================================
    @PostMapping("/manual")
    public ResponseEntity<UserDTO> createManual(@Valid @RequestBody UserCreateDTO dto) {
        // 1. El servicio ahora devuelve directamente el UserDTO mapeado dentro de la transacción
        UserDTO createdUser = userService.createUserManual(dto);
        
        // 2. Respondemos con 201 Created y el cuerpo ya procesado
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // =====================================
    // 🔹 Buscar por username
    // =====================================
    @GetMapping("/search/username")
    public ResponseEntity<Page<UserDTO>> searchByUsername(@RequestParam String value, Pageable pageable) {
        return ResponseEntity.ok(userService.findByUsernameContaining(value, pageable));
    }

    // =====================================
    // 🔹 Buscar por username o email
    // =====================================
    @GetMapping("/search")
    public ResponseEntity<Page<UserDTO>> searchByUsernameOrEmail(@RequestParam String value, Pageable pageable) {
        return ResponseEntity.ok(userService.findByUsernameOrEmail(value, pageable));
    }
}
