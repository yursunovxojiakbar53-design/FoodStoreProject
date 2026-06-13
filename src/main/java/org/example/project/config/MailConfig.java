package org.example.project.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    private static final Logger log = LoggerFactory.getLogger(MailConfig.class);

    @Bean
    public JavaMailSender javaMailSender(Environment environment) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(firstNotBlank(
                environment.getProperty("MAIL_HOST"),
                environment.getProperty("spring.mail.host"),
                "smtp.gmail.com"
        ));
        mailSender.setPort(parsePort(firstNotBlank(
                environment.getProperty("MAIL_PORT"),
                environment.getProperty("spring.mail.port"),
                "587"
        )));
        mailSender.setUsername(firstNotBlank(
                environment.getProperty("MAIL_USERNAME"),
                environment.getProperty("spring.mail.username"),
                environment.getProperty("app.mail.fallback-username")
        ));
        mailSender.setPassword(firstNotBlank(
                environment.getProperty("MAIL_PASSWORD"),
                environment.getProperty("spring.mail.password"),
                environment.getProperty("app.mail.fallback-password")
        ));
        mailSender.setDefaultEncoding("UTF-8");

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.mime.charset", "UTF-8");
        properties.put("mail.smtp.auth", environment.getProperty("spring.mail.properties.mail.smtp.auth", "true"));
        properties.put("mail.smtp.starttls.enable", environment.getProperty("spring.mail.properties.mail.smtp.starttls.enable", "true"));
        properties.put("mail.smtp.starttls.required", environment.getProperty("spring.mail.properties.mail.smtp.starttls.required", "true"));
        properties.put("mail.smtp.connectiontimeout", environment.getProperty("spring.mail.properties.mail.smtp.connectiontimeout", "5000"));
        properties.put("mail.smtp.timeout", environment.getProperty("spring.mail.properties.mail.smtp.timeout", "5000"));
        properties.put("mail.smtp.writetimeout", environment.getProperty("spring.mail.properties.mail.smtp.writetimeout", "5000"));
        properties.put("mail.debug", environment.getProperty("spring.mail.properties.mail.debug", "false"));

        if (isBlank(mailSender.getUsername())) {
            log.error("Email SMTP username is empty. Set MAIL_USERNAME or app.mail.fallback-username.");
        }
        if (isBlank(mailSender.getPassword())) {
            log.error("Email SMTP password is empty. Set MAIL_PASSWORD or app.mail.fallback-password.");
        }

        return mailSender;
    }

    private static String firstNotBlank(String... values) {
        for (String value : values) {
            if (!isBlank(value)) {
                return value.trim();
            }
        }
        return "";
    }

    private static int parsePort(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            log.error("Invalid MAIL_PORT/spring.mail.port value '{}'. Falling back to 587.", value);
            return 587;
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
