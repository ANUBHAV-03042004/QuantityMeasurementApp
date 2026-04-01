package com.app.quantitymeasurementapp.user;

import com.app.quantitymeasurementapp.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User management endpoints.
 *
 *  GET  /api/v1/users/me          — any authenticated user (own profile)
 *  GET  /api/v1/users             — ADMIN only (list all users)
 *  PUT  /api/v1/users/{id}/role   — ADMIN only (change user role)
 *  DELETE /api/v1/users/{id}      — ADMIN only (delete user)
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "Profile and admin user management")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    // ── Own profile ───────────────────────────────────────────────────────────

    @GetMapping("/me")
    @Operation(summary = "Get the currently authenticated user's profile")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(UserProfileResponse.from(user));
    }

    // ── Admin — list all ──────────────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "ADMIN: List all registered users")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        List<UserProfileResponse> users = userService.findAll()
                .stream()
                .map(UserProfileResponse::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    // ── Admin — change role ───────────────────────────────────────────────────

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "ADMIN: Change a user's role (USER / ADMIN)")
    public ResponseEntity<UserProfileResponse> updateRole(
            @PathVariable Long id,
            @RequestParam User.Role role) {

        User updated = userService.updateRole(id, role);
        return ResponseEntity.ok(UserProfileResponse.from(updated));
    }

    // ── Admin — delete ────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "ADMIN: Delete a user by ID")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ── Response DTO (never expose password) ─────────────────────────────────

    public record UserProfileResponse(
            Long   id,
            String firstName,
            String lastName,
            String email,
            String role,
            String authProvider
    ) {
        static UserProfileResponse from(User u) {
            return new UserProfileResponse(
                    u.getId(),
                    u.getFirstName(),
                    u.getLastName(),
                    u.getEmail(),
                    u.getRole().name(),
                    u.getAuthProvider().name()
            );
        }
    }
}
