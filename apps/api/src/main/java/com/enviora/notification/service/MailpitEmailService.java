package com.enviora.notification.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailpitEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(MailpitEmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.security.email-verification.expiration-minutes:15}")
    private int expirationMinutes;

    @Value("${spring.mail.from:noreply@enviora.com}")
    private String mailFrom;

    public MailpitEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationEmail(String toEmail, String recipientName, String rawToken) {
        String verificationUrl = frontendUrl + "/verify-email?token=" + rawToken;

        // Print bright yellow highlighted terminal banner for local development
        String yellowHighlighter = "\n" +
            "\u001B[33;1m========================================================================================\u001B[0m\n" +
            "\u001B[43m\u001B[30m [LOCAL DEV VERIFICATION EMAIL LINK] \u001B[0m  \u001B[36;1mRecipient:\u001B[0m " + toEmail + "\n" +
            "\u001B[33;1mCLICK OR COPY THIS LINK TO VERIFY ACCOUNT:\u001B[0m\n" +
            "\u001B[43m\u001B[30m " + verificationUrl + " \u001B[0m\n" +
            "\u001B[33;1m========================================================================================\u001B[0m\n";

        System.out.println(yellowHighlighter);
        log.info("[LOCAL DEV VERIFICATION LINK] User: {} | Link: {}", toEmail, verificationUrl);

        String htmlContent = buildEmailHtml(recipientName, verificationUrl, expirationMinutes);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailFrom);
            helper.setTo(toEmail);
            helper.setSubject("Verify your Enviora account");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Verification email successfully dispatched to recipient: {}", toEmail);
        } catch (Exception e) {
            log.warn("SMTP server not active. Local verification link printed above for testing.");
        }
    }

    private String buildEmailHtml(String name, String url, int expiration) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="utf-8">
              <style>
                body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background-color: #0f172a; color: #f8fafc; margin: 0; padding: 24px; }
                .container { max-width: 560px; margin: 0 auto; background-color: #1e293b; border-radius: 12px; border: 1px solid #334155; padding: 32px; }
                .brand { font-size: 20px; font-weight: 700; color: #38bdf8; margin-bottom: 24px; display: inline-block; }
                .button { display: inline-block; background-color: #0284c7; color: #ffffff; text-decoration: none; padding: 12px 24px; border-radius: 8px; font-weight: 600; margin: 24px 0; }
                .footer { font-size: 12px; color: #94a3b8; border-top: 1px solid #334155; margin-top: 32px; padding-top: 16px; }
              </style>
            </head>
            <body>
              <div class="container">
                <div class="brand">Enviora — Developer Secrets Platform</div>
                <h2>Verify your email address</h2>
                <p>Hello %s,</p>
                <p>Thank you for registering with Enviora. Please click the button below to verify your email address and activate your account:</p>
                <a href="%s" class="button">Verify Email Address</a>
                <p style="font-size: 13px; color: #cbd5e1;">This link will expire in %d minutes.</p>
                <p style="font-size: 13px; color: #cbd5e1;">If you did not create an account on Enviora, no further action is required.</p>
                <div class="footer">
                  <p>Security Notice: Never share this verification link with anyone.</p>
                  <p>&copy; Enviora Security Team</p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(name != null ? name : "Developer", url, expiration);
    }
}
