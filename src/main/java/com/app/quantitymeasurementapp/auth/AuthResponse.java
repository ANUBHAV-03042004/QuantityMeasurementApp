package com.app.quantitymeasurementapp.auth;

import com.app.quantitymeasurementapp.auth.AuthResponse.Builder;

public class AuthResponse {

    private String token;
    private String tokenType;
    private String email;
    private String role;
    private long   expiresInSeconds;

    public AuthResponse() {}

    public AuthResponse(String token, String tokenType, String email,
                        String role, long expiresInSeconds) {
        this.token            = token;
        this.tokenType        = tokenType;
        this.email            = email;
        this.role             = role;
        this.expiresInSeconds = expiresInSeconds;
    }

    // Getters
    public String getToken()            { return token; }
    public String getTokenType()        { return tokenType; }
    public String getEmail()            { return email; }
    public String getRole()             { return role; }
    public long   getExpiresInSeconds() { return expiresInSeconds; }

    // Setters
    public void setToken(String token)                        { this.token = token; }
    public void setTokenType(String tokenType)                { this.tokenType = tokenType; }
    public void setEmail(String email)                        { this.email = email; }
    public void setRole(String role)                          { this.role = role; }
    public void setExpiresInSeconds(long expiresInSeconds)    { this.expiresInSeconds = expiresInSeconds; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String token;
        private String tokenType;
        private String email;
        private String role;
        private long   expiresInSeconds;

        private Builder() {}

        public Builder token(String token)                      { this.token = token;                       return this; }
        public Builder tokenType(String tokenType)              { this.tokenType = tokenType;               return this; }
        public Builder email(String email)                      { this.email = email;                       return this; }
        public Builder role(String role)                        { this.role = role;                         return this; }
        public Builder expiresInSeconds(long expiresInSeconds)  { this.expiresInSeconds = expiresInSeconds; return this; }

        public AuthResponse build() {
            return new AuthResponse(token, tokenType, email, role, expiresInSeconds);
        }
    }
}
