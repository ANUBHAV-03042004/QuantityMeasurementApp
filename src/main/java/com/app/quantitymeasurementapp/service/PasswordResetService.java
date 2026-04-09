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
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);

    @Value("${app.password-reset.token-expiry-minutes:30}")
    private int expiryMinutes;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.mail.from-name:Quantra}")
    private String fromName;

    /**
     * Base URL of the frontend — used to build the reset link in the email.
     * This is overridden per-request using the Origin header so both frontends
     * get the correct link. This value is only used as a last-resort fallback
     * (e.g. when calling from Swagger with no Origin header).
     */
    @Value("${app.frontend.base-url:https://anubhav-03042004.github.io/QuantityMeasurementApp-Frontend}")
    private String fallbackFrontendBaseUrl;

    private final UserRepository              userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder             passwordEncoder;
    private final JavaMailSender              mailSender;

    public PasswordResetService(UserRepository              userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                PasswordEncoder             passwordEncoder,
                                JavaMailSender              mailSender) {
        this.userRepository  = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender      = mailSender;
    }

    /**
     * Generate a reset token and email the link.
     * @param email       user's email
     * @param frontendUrl base URL of the calling frontend (from Origin header),
     *                    or null to use the configured fallback
     */
    @Transactional
    public void initiatePasswordReset(String email, String frontendUrl) {
        Optional<User> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) {
            log.info("Forgot-password requested for unknown email: {}", email);
            return; // always return success to prevent user enumeration
        }

        User user = optUser.get();

        // OAuth2-only accounts have no password — skip silently
        if (user.getAuthProvider() == User.AuthProvider.AUTH_GOOGLE
                && (user.getPassword() == null || user.getPassword().isBlank())) {
            log.info("Forgot-password ignored for Google-only account: {}", email);
            return;
        }

        // Invalidate existing tokens for this user
        tokenRepository.deleteByUserId(user.getId());

        // Generate a secure random token
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        tokenRepository.save(new PasswordResetToken(
                rawToken, user, LocalDateTime.now().plusMinutes(expiryMinutes)));

        // Build reset link — use caller's frontend base URL, fall back to configured default
        String base = (frontendUrl != null && !frontendUrl.isBlank())
                ? frontendUrl : fallbackFrontendBaseUrl;

        // Angular uses route /reset-password; legacy HTML uses reset-password.html
        // We append /reset-password and let each frontend handle it.
        String resetLink = base.endsWith("/")
                ? base + "reset-password?token=" + rawToken
                : base + "/reset-password?token=" + rawToken;

        sendResetEmail(user.getEmail(), user.getFirstName(), resetLink);
        log.info("Password reset email sent to {}", email);
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        PasswordResetToken prt = tokenRepository.findByToken(rawToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        if (prt.isUsed())    throw new IllegalArgumentException("Reset token has already been used");
        if (prt.isExpired()) throw new IllegalArgumentException("Reset token has expired — request a new one");

        User user = prt.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        prt.setUsed(true);
        tokenRepository.save(prt);
        log.info("Password reset successfully for {}", user.getEmail());
    }

    private void sendResetEmail(String toEmail, String firstName, String resetLink) {
        SimpleMailMessage msg = new SimpleMailMessage();
        // Gmail SMTP requires plain address in From — "Name <email>" format causes MailSendException
        msg.setFrom(fromAddress);
        msg.setTo(toEmail);
        msg.setSubject("🐲 Quantra — Reset Your Password");
        msg.setText(
            "Hello " + firstName + ",\n\n"
            + "A password reset was requested for your Quantra account.\n\n"
            + "Click the link below to reset your password (valid for " + expiryMinutes + " minutes):\n\n"
            + resetLink + "\n\n"
            + "If you did not request this, you can safely ignore this email.\n\n"
            + "— The Quantra Dragon Kingdom 🐲"
        );
        mailSender.send(msg);
    }
}