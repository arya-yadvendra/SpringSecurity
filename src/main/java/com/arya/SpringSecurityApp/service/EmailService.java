package com.arya.SpringSecurityApp.service;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;


@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${email.schedule.cron}")
    private String configuredCronExpression;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @PostConstruct
    public void init() {
        String cronExpression = configuredCronExpression;
    }

    public void sendEmail(String to, String subject, String text) throws IOException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            File file = new File("/Users/arya_yadvendra/SpringSecurity/pic.jpg");
            if (file.exists() && file.isFile()) {
                helper.addAttachment(file.getName(), file);
            } else {
                throw new MessagingException("Attachment not found or is a directory: " + "/Users/arya_yadvendra/SpringSecurity");
            }

            javaMailSender.send(message);
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public ResponseEntity<String> scheduleEmail(String to, String subject, String text) throws IOException, MessagingException {
        sendEmail(to, subject, text);
        return ResponseEntity.ok("Email sent successfully to: " + to);
    }
}