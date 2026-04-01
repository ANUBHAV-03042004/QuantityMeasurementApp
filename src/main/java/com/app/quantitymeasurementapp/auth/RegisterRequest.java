package com.app.quantitymeasurementapp.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RegisterRequest {

	    @NotBlank(message = "First name is required")
	    private String firstName;

	    @NotBlank(message = "Last name is required")
	    private String lastName;

	    @NotBlank(message = "Email is required")
	    @Email(message = "Must be a valid email")
	    private String email;

	    @NotBlank(message = "Password is required")
	    @Pattern(
	        regexp  = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$",
	        message = "Password must be 8+ chars with uppercase, lowercase, digit and special character"
	    )
	    private String password;

	    public RegisterRequest() {}

	    public String getFirstName() { return firstName; }
	    public String getLastName()  { return lastName; }
	    public String getEmail()     { return email; }
	    public String getPassword()  { return password; }

	    public void setFirstName(String v) { this.firstName = v; }
	    public void setLastName(String v)  { this.lastName  = v; }
	    public void setEmail(String v)     { this.email     = v; }
	    public void setPassword(String v)  { this.password  = v; }
	}