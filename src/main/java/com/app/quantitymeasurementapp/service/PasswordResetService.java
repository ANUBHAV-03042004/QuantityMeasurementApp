package com.app.quantitymeasurementapp.service;

import com.app.quantitymeasurementapp.repository.PasswordResetToken;
import com.app.quantitymeasurementapp.repository.PasswordResetTokenRepository;
import com.app.quantitymeasurementapp.user.User;
import com.app.quantitymeasurementapp.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    private static final int    TOKEN_EXPIRY_MINUTES = 30;

    private final UserRepository              userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender              mailSender;
    private final PasswordEncoder             passwordEncoder;

    @Value("${app.frontend.url:https://anubhav-03042004.github.io/QuantityMeasurementFrontend}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public PasswordResetService(UserRepository              userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                JavaMailSender              mailSender,
                                PasswordEncoder             passwordEncoder) {
        this.userRepository  = userRepository;
        this.tokenRepository = tokenRepository;
        this.mailSender      = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    // ── Step 1: Generate token and send email ─────────────────────────────────

    public void initiateReset(String email) {
        // Always respond with success to prevent user enumeration
        userRepository.findByEmail(email).ifPresent(user -> {
            // Delete any existing tokens for this user
            tokenRepository.deleteAllByUserId(user.getId());

            String token = UUID.randomUUID().toString();
            LocalDateTime expiry = LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES);
            tokenRepository.save(new PasswordResetToken(token, user, expiry));

            sendResetEmail(user, token);
            log.info("Password reset token issued for {}", email);
        });
    }

    private void sendResetEmail(User user, String token) {
        String resetLink = frontendUrl + "/reset-password.html?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Quantra — Reset your password");
        message.setText(
            "Hi " + user.getFirstName() + ",\n\n"
            + "You requested a password reset for your Quantra account.\n\n"
            + "Click the link below to set a new password. "
            + "This link expires in " + TOKEN_EXPIRY_MINUTES + " minutes.\n\n"
            + resetLink + "\n\n"
            + "If you didn't request this, you can safely ignore this email.\n\n"
            + "— The Quantra Team"
        );

        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send reset email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    // ── Step 2: Validate token and update password ────────────────────────────

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken prt = tokenRepository.findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset link."));

        if (prt.isUsed())    throw new IllegalArgumentException("This reset link has already been used.");
        if (prt.isExpired()) throw new IllegalArgumentException("This reset link has expired. Please request a new one.");

        User user = prt.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        prt.setUsed(true);
        tokenRepository.save(prt);
        log.info("Password reset successfully for {}", user.getEmail());
    }

    // ── Validate token only (for frontend to check before showing form) ───────

    public boolean isTokenValid(String token) {
        return tokenRepository.findByToken(token)
            .map(t -> !t.isUsed() && !t.isExpired())
            .orElse(false);
    }
}
