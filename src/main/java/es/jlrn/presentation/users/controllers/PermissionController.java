package es.jlrn.presentation.users.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.jlrn.persistence.models.users.models.PermissionEntity;
import es.jlrn.presentation.users.services.interfaces.IpermissionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PermissionController {
//
    private final IpermissionService permissionService;

    @GetMapping
    public ResponseEntity<List<PermissionEntity>> getAll() {
        return ResponseEntity.ok(permissionService.findAll());
    }

    @PostMapping
    public ResponseEntity<PermissionEntity> create(@RequestBody PermissionEntity permission) {
        return ResponseEntity.ok(permissionService.save(permission));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        permissionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}