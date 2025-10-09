package com.app.inventory.service.impl;

import com.app.inventory.model.User;
import com.app.inventory.service.EmailService;
import com.app.inventory.service.VerificationTokenService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
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
public class EmailServiceImpl implements EmailService {

    private final VerificationTokenService verificationTokenService;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.verification.uri}")
    private String verificationUri;

    @Override
    @Async
    public void sendAccountActivationEmail(User user) {
        String token = verificationTokenService.create(user);
        sendEmail(user.getEmail(), "Account Created", verificationUri + token);
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
            System.out.println("Verification email sent to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}
