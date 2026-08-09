package com.enviora.notification.service;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String recipientName, String rawToken);
}
