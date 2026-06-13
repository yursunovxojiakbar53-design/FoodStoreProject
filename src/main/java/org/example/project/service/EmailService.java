package org.example.project.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @PostConstruct
    void validateMailCredentials() {
        if (!isMailConfigured()) {
            log.error("Email service is not configured: SMTP username or password is empty. Set MAIL_USERNAME and MAIL_PASSWORD.");
        }
    }

    public void sendVerificationEmail(String toEmail, Integer code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Verification Email");
        message.setText("Tasdiqlash kodingiz: " + code +
                "\n\nQuyidagi linkka o'ting: " +
                "http://localhost:8080/api/auth/verify?email=" + toEmail + "&code=" + code);

        sendSafely(message, toEmail, "verification");
    }

    public void sendOrderConfirmation(String toEmail, Integer orderId, double totalPrice) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Buyurtmangiz qabul qilindi - #" + orderId);
        message.setText(
                "Hurmatli mijoz!\n\n" +
                        "Buyurtmangiz muvaffaqiyatli qabul qilindi.\n" +
                        "Buyurtma raqami: #" + orderId + "\n" +
                        "Jami summa: " + totalPrice + " so'm\n\n" +
                        "Buyurtmangiz holati haqida xabardor qilamiz."
        );

        sendSafely(message, toEmail, "order confirmation");
    }

    public void sendOrderStatusUpdate(String toEmail, Integer orderId, String status) {
        String statusText = switch (status) {
            case "PENDING" -> "Buyurtmangiz kutilmoqda";
            case "PROCESSING" -> "Buyurtmangiz tayyorlanmoqda";
            case "CONFIRMED" -> "Buyurtmangiz tasdiqlandi, tayyorlanmoqda";
            case "ON_THE_WAY" -> "Kuryer yo'lda, tez orada yetib keladi";
            case "DELIVERED" -> "Buyurtmangiz yetkazildi!";
            case "CANCELED", "CANCELLED" -> "Buyurtmangiz bekor qilindi";
            default -> "Buyurtma holati yangilandi: " + status;
        };

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Buyurtma #" + orderId + " holati yangilandi");
        message.setText(
                "Hurmatli mijoz!\n\n" +
                        statusText + "\n" +
                        "Buyurtma raqami: #" + orderId
        );

        sendSafely(message, toEmail, "order status update");
    }

    private void sendSafely(SimpleMailMessage message, String toEmail, String emailType) {
        if (isBlank(toEmail)) {
            log.warn("Email not sent: recipient is empty for {} email.", emailType);
            return;
        }

        if (!isMailConfigured()) {
            log.error("Email not sent to {}: SMTP username or password is empty. Set MAIL_USERNAME and MAIL_PASSWORD.", toEmail);
            return;
        }

        try {
            mailSender.send(message);
            log.info("{} email sent to {}", emailType, toEmail);
        } catch (MailException ex) {
            log.error("Failed to send {} email to {}: {}", emailType, toEmail, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Unexpected error while sending {} email to {}: {}", emailType, toEmail, ex.getMessage(), ex);
        }
    }

    private boolean isMailConfigured() {
        if (mailSender instanceof JavaMailSenderImpl javaMailSender) {
            return !isBlank(javaMailSender.getUsername()) && !isBlank(javaMailSender.getPassword());
        }
        return true;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
