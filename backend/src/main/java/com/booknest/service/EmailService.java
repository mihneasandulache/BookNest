package com.booknest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendWelcomeEmail(String toEmail, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@booknest.com");
        message.setTo(toEmail);
        message.setSubject("Welcome to BookNest!");
        message.setText("Hi " + username + ",\n\nWelcome to BookNest! Start exploring and reviewing books.\n\nHappy reading!");
        mailSender.send(message);
    }

    @Async
    public void sendFeedbackConfirmationEmail(String toEmail, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("We received your feedback!");
        message.setText("Hi " + username + ",\n\nThank you for your feedback! We appreciate you taking the time to share your thoughts.\n\nThe BookNest Team");
        mailSender.send(message);
    }
}
