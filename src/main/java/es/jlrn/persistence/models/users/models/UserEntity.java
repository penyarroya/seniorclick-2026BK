package es.jlrn.persistence.models.users.models;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;

import es.jlrn.persistence.models.universilabs.models.UserProfile;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarios")
public class UserEntity implements UserDetails {
//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 40)
    @Column(name="username", length = 40, nullable = false, unique = true)
    private String username;

    @JsonIgnore
    @NotBlank
    @Size(min = 8)
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Email
    @Size(max = 150)
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private boolean activo;

    @Column(nullable = false)
    private boolean emailVerified;

    @Column(length = 6)
    private String verificationCode;

    @Column
    private LocalDateTime verificationCodeExpiry;

    @Column(length = 512) // ¡Suficientemente largo para un JWT!
    private String passwordResetToken; 

    @Column
    private LocalDateTime passwordResetTokenExpiry;

    // -------------------- NUEVO CAMPO --------------------
    @Column(nullable = false)
    @Builder.Default
    private Boolean passwordResetConfirmed = false;

    // -------------------- CAMPOS PARA RESET POR CÓDIGO (OTP) --------------------
    @Column(length = 6)
    private String passwordResetCode;

    @Column
    private LocalDateTime passwordResetCodeExpiry;


    @NotEmpty
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Builder.Default
    private Set<RoleEntity> roles = new HashSet<>();

    // nuevo campo 
    // @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore 
    private UserProfile profile;

    // Auditoría
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaAlta;

    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;

    @Version
    private Long version;

    // Métodos de Spring Security
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        roles.forEach(role -> authorities.add(
            new SimpleGrantedAuthority("ROLE_" + role.getName())
        ));

        roles.stream()
            .flatMap(role -> role.getPermissions().stream())
            .forEach(permission -> authorities.add(
                new SimpleGrantedAuthority(permission.getName())
            ));

        return authorities;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return activo; }

    @PrePersist
    @PreUpdate
    private void normalizeFields() {
        if (email != null) email = email.trim().toLowerCase();
        if (username != null) username = username.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity)) return false;
        return id != null && id.equals(((UserEntity) o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
