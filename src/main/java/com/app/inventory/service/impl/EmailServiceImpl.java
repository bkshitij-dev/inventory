package com.app.inventory.service.impl;

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

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.verification.uri}")
    private String verificationUri;

    @Override
    @Async
    public void sendAccountActivationEmail(String email, String token) {
        sendEmail(email, "Account Created", verificationUri + token);
    }

    private void sendEmail(String to, String subject, String verificationLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setSentDate(new Date());
            helper.setTo(to);
            helper.setSubject(subject);

            ClassPathResource resource = new ClassPathResource("templates/verify.html");
            String htmlContent = Files.readString(Path.of(resource.getURI()));
            htmlContent = htmlContent.replace("{{verificationLink}}", verificationLink);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Verification email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}. Reason: {}", to, e.getMessage(), e);
        }
    }
}
