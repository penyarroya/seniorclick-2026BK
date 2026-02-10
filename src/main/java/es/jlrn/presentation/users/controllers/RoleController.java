package es.jlrn.presentation.users.controllers;

import es.jlrn.presentation.users.dtos.Roles.RoleDTO;
import es.jlrn.presentation.users.services.interfaces.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
@CrossOrigin(origins = "http://localhost:4200") // Para conectar con Angular
public class RoleController {
//
    private final IRoleService roleService;

    // 1. Obtener todos los roles (Endpoint principal para tu combo de Angular)
    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    // 2. Endpoint específico para obtener solo los nombres (Opcional, muy útil para combos)
    @GetMapping("/names")
    public ResponseEntity<List<String>> getRoleNames() {
        List<String> names = roleService.getAllRoles()
                .stream()
                .map(RoleDTO::getName)
                .collect(Collectors.toList());
        return ResponseEntity.ok(names);
    }

    // 3. Obtener un rol por ID
    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getById(@PathVariable Long id) {
        return roleService.getRoleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. Crear un nuevo rol
    @PostMapping
    public ResponseEntity<RoleDTO> create(@RequestBody RoleDTO roleDTO) {
        return new ResponseEntity<>(roleService.createRole(roleDTO), HttpStatus.CREATED);
    }

    // 5. Actualizar un rol
    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> update(@PathVariable Long id, @RequestBody RoleDTO roleDTO) {
        return ResponseEntity.ok(roleService.updateRole(id, roleDTO));
    }

    // 6. Eliminar un rol
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}