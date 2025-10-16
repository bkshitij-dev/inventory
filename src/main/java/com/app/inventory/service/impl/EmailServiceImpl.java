package com.app.inventory.service.impl;

import com.app.inventory.constant.MessageConstants;
import com.app.inventory.enums.TokenType;
import com.app.inventory.service.CustomMessageSource;
import com.app.inventory.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final CustomMessageSource customMessageSource;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.verification.uri}")
    private String verificationUri;

    @Value("${app.passwordReset.uri}")
    private String resetPasswordUri;

    @Override
    @Async
    public void sendEmail(String email, String token, TokenType tokenType) {
        switch (tokenType) {
            case ACCOUNT_VERIFICATION -> sendAccountActivationEmail(email, token);
            case PASSWORD_RESET -> sendResetPasswordEmail(email, token);
            default -> {
                log.error(customMessageSource.getMessage(MessageConstants.EAT_TOKEN_TYPE_INVALID));
            }
        };
    }

    private void sendAccountActivationEmail(String email, String token) {
        send(email, customMessageSource.getMessage(MessageConstants.EMAIL_SUBJECT_ACCOUNT_CREATED),
                verificationUri + token, "verify.html");
    }

    private void sendResetPasswordEmail(String email, String token) {
        send(email, customMessageSource.getMessage(MessageConstants.EMAIL_SUBJECT_RESET_PASSWORD),
                resetPasswordUri + token, "password-reset.html");
    }

    private void send(String to, String subject, String link, String templateName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setSentDate(new Date());
            helper.setTo(to);
            helper.setSubject(subject);

            ClassPathResource resource = new ClassPathResource("templates/" + templateName);
            String htmlContent = Files.readString(Path.of(resource.getURI()));
            htmlContent = htmlContent.replace("{{link}}", link);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}. Reason: {}", to, e.getMessage(), e);
        }
    }
}
