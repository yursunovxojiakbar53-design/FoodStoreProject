package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Verification Email");
        message.setText("Tasdiqlash kodingiz: " + code +
                "\n\nQuyidagi linkka o'ting: " +
                "http://localhost:8080/api/auth/verify?email=" + toEmail + "&code=" + code);
        mailSender.send(message);
    }

}
