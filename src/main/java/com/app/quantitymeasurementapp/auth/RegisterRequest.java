package com.app.quantitymeasurementapp.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    /**
     * Regex: RFC-5322 inspired email validation.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    @Pattern(
        regexp  = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$",
        message = "Email format is invalid"
    )
    private String email;

    /**
     * Regex: 8–64 chars, at least one uppercase letter, one lowercase letter,
     *         one digit and one special character (@#$%^&+=!).
     */
    @NotBlank(message = "Password is required")
    @Pattern(
        regexp  = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,64}$",
        message = "Password must be 8-64 chars with uppercase, lowercase, digit and special character"
    )
    private String password;
}
