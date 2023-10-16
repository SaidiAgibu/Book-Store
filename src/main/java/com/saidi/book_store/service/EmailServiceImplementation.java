package com.saidi.book_store.service;

import com.saidi.book_store.utils.EmailUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Data
@Service
public class EmailServiceImplementation implements EmailService{
    private HttpServletRequest request;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${VERIFY_EMAIL_HOST}")
    private String host;
    private JavaMailSender javaMailSender;

    @Autowired
    public EmailServiceImplementation(JavaMailSender javaMailSender,HttpServletRequest request) {
        this.javaMailSender = javaMailSender;
        this.request = request;

    }

    @Override
    @Async
    public void sendEmailToUser(String name, String to, String token) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject("S-Library Book Store Account Verification");
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setText(EmailUtil.getEmailMessage(name, host, token));
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
