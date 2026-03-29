package com.app.quantitymeasurementapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends transactional emails using Spring Mail (JavaMailSender).
 * In dev profile (application-dev.properties), SMTP is configured
 * to point at Mailhog (localhost:1025) so no real emails are sent.
 *
 * All send failures are caught and logged — they must NOT interrupt
 * the main business flow.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@quantitymeasurementapp.com}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a welcome email to a newly registered user.
     */
    public void sendWelcomeEmail(String toEmail, String firstName) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromAddress);
            msg.setTo(toEmail);
            msg.setSubject("Welcome to Quantity Measurement App!");
            msg.setText(
                "Hi " + firstName + ",\n\n"
                + "Welcome to the Quantity Measurement App!\n\n"
                + "Your account has been created successfully.\n"
                + "You can now login and start converting, comparing and "
                + "performing arithmetic on quantities.\n\n"
                + "Happy measuring!\n"
                + "— The QM Team"
            );
            mailSender.send(msg);
            log.info("Welcome email sent to {}", toEmail);
        } catch (MailException e) {
            log.warn("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Sends a measurement summary notification.
     */
    public void sendMeasurementNotification(String toEmail, String operation, String result) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromAddress);
            msg.setTo(toEmail);
            msg.setSubject("Measurement Result: " + operation);
            msg.setText(
                "Your " + operation + " operation completed.\n"
                + "Result: " + result + "\n\n"
                + "— The QM Team"
            );
            mailSender.send(msg);
            log.info("Measurement notification sent to {}", toEmail);
        } catch (MailException e) {
            log.warn("Failed to send measurement notification to {}: {}", toEmail, e.getMessage());
        }
    }
}
