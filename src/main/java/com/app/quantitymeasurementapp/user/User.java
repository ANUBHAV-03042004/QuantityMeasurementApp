package com.app.quantitymeasurementapp.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a registered user of the application.
 * Email and password are validated with regex before persistence.
 */
@Entity
@Table(name = "users",
       indexes = { @Index(name = "idx_user_email", columnList = "email", unique = true) })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(nullable = false)
    private String lastName;

    /**
     * Regex: standard RFC-5322-inspired email pattern.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid address")
    @Pattern(
        regexp  = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$",
        message = "Email format is invalid"
    )
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Stored as BCrypt hash. Raw password is validated at registration:
     * min 8 chars, at least 1 uppercase, 1 lowercase, 1 digit, 1 special character.
     */
    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;   // BCrypt-hashed

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Role { USER, ADMIN }
}
