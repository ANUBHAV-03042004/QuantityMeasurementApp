package com.app.quantitymeasurementapp.repository;

import com.app.quantitymeasurementapp.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public PasswordResetToken() {}

    public PasswordResetToken(String token, User user, LocalDateTime expiresAt) {
        this.token = token; this.user = user; this.expiresAt = expiresAt;
    }

    public boolean isExpired() { return LocalDateTime.now().isAfter(expiresAt); }

    public Long          getId()        { return id; }
    public String        getToken()     { return token; }
    public User          getUser()      { return user; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean       isUsed()       { return used; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setToken(String t)          { this.token = t; }
    public void setUser(User u)             { this.user = u; }
    public void setExpiresAt(LocalDateTime e){ this.expiresAt = e; }
    public void setUsed(boolean u)          { this.used = u; }
}
