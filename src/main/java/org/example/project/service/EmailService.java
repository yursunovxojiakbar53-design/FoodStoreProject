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

    // Buyurtma yaratilganda xabar
    public void sendOrderConfirmation(String toEmail, Integer orderId, double totalPrice) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Buyurtmangiz qabul qilindi — #" + orderId);
        message.setText(
                "Hurmatli mijoz!\n\n" +
                        "Buyurtmangiz muvaffaqiyatli qabul qilindi.\n" +
                        "Buyurtma raqami: #" + orderId + "\n" +
                        "Jami summa: " + totalPrice + " so'm\n\n" +
                        "Buyurtmangiz holati haqida xabardor qilamiz."
        );
        mailSender.send(message);
    }


    // Buyurtma statusi o'zgarganda xabar
    public void sendOrderStatusUpdate(String toEmail, Integer orderId, String status) {
        String statusText = switch (status) {
            case "PENDING"    -> "Buyurtmangiz kutilmoqda";
            case "CONFIRMED"  -> "Buyurtmangiz tasdiqlandi, tayyorlanmoqda";
            case "ON_THE_WAY" -> "Kuryer yo'lda, tez orada yetib keladi";
            case "DELIVERED"  -> "Buyurtmangiz yetkazildi!";
            case "CANCELED"   -> "Buyurtmangiz bekor qilindi";
            default           -> "Buyurtma holati yangilandi: " + status;
        };
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Buyurtma #" + orderId + " holati yangilandi");
        message.setText(
                "Hurmatli mijoz!\n\n" +
                        statusText + "\n" +
                        "Buyurtma raqami: #" + orderId
        );
        mailSender.send(message);
    }
}
