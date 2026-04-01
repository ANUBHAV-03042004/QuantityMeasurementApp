package com.app.quantitymeasurementapp.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

/**
 * Represents a registered user.
 * Created via email/password signup OR auto-provisioned on first Google OAuth2 login.
 *
 * NOTE: No Lombok — all getters, setters, and builder written manually.
 */
@Entity
@Table(
    name = "users",
    indexes = { @Index(name = "idx_user_email", columnList = "email", unique = true) }
)
public class User {

    // ── Fields ────────────────────────────────────────────────────────────────

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String firstName;

    @NotBlank
    @Column(nullable = false)
    private String lastName;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    /** BCrypt hash — null for OAuth2-only users */
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    /**
     * AUTH_LOCAL  → registered with email + password
     * AUTH_GOOGLE → registered via Google OAuth2
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider = AuthProvider.AUTH_LOCAL;

    /** Google's "sub" claim — stored so we can look up returning OAuth2 users */
    @Column(unique = true)
    private String googleId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // ── Lifecycle hooks ───────────────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ── Enums ─────────────────────────────────────────────────────────────────

    public enum Role         { USER, ADMIN }
    public enum AuthProvider { AUTH_LOCAL, AUTH_GOOGLE }

    // ── Constructors ──────────────────────────────────────────────────────────

    public User() {}

    /** Full constructor used internally */
    public User(Long id, String firstName, String lastName, String email,
                String password, Role role, AuthProvider authProvider,
                String googleId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id           = id;
        this.firstName    = firstName;
        this.lastName     = lastName;
        this.email        = email;
        this.password     = password;
        this.role         = role;
        this.authProvider = authProvider;
        this.googleId     = googleId;
        this.createdAt    = createdAt;
        this.updatedAt    = updatedAt;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public Long          getId()           { return id; }
    public String        getFirstName()    { return firstName; }
    public String        getLastName()     { return lastName; }
    public String        getEmail()        { return email; }
    public String        getPassword()     { return password; }
    public Role          getRole()         { return role; }
    public AuthProvider  getAuthProvider() { return authProvider; }
    public String        getGoogleId()     { return googleId; }
    public LocalDateTime getCreatedAt()    { return createdAt; }
    public LocalDateTime getUpdatedAt()    { return updatedAt; }

    // ── Setters ───────────────────────────────────────────────────────────────

    public void setId(Long id)                        { this.id = id; }
    public void setFirstName(String firstName)        { this.firstName = firstName; }
    public void setLastName(String lastName)          { this.lastName = lastName; }
    public void setEmail(String email)                { this.email = email; }
    public void setPassword(String password)          { this.password = password; }
    public void setRole(Role role)                    { this.role = role; }
    public void setAuthProvider(AuthProvider ap)      { this.authProvider = ap; }
    public void setGoogleId(String googleId)          { this.googleId = googleId; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ── Builder ───────────────────────────────────────────────────────────────

    public static Builder builder() { return new Builder(); }

    public static class Builder {

        private Long         id;
        private String       firstName;
        private String       lastName;
        private String       email;
        private String       password;
        private Role         role         = Role.USER;
        private AuthProvider authProvider = AuthProvider.AUTH_LOCAL;
        private String       googleId;

        private Builder() {}

        public Builder id(Long id)                        { this.id = id;                   return this; }
        public Builder firstName(String firstName)        { this.firstName = firstName;     return this; }
        public Builder lastName(String lastName)          { this.lastName = lastName;       return this; }
        public Builder email(String email)                { this.email = email;             return this; }
        public Builder password(String password)          { this.password = password;       return this; }
        public Builder role(Role role)                    { this.role = role;               return this; }
        public Builder authProvider(AuthProvider ap)      { this.authProvider = ap;         return this; }
        public Builder googleId(String googleId)          { this.googleId = googleId;       return this; }

        public User build() {
            User u = new User();
            u.id           = this.id;
            u.firstName    = this.firstName;
            u.lastName     = this.lastName;
            u.email        = this.email;
            u.password     = this.password;
            u.role         = this.role;
            u.authProvider = this.authProvider;
            u.googleId     = this.googleId;
            return u;
        }
    }

    // ── toString ──────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "User{id=" + id
               + ", email='" + email + "'"
               + ", role=" + role
               + ", authProvider=" + authProvider + "}";
    }
}
