package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.services.interfaces.IEmailSender;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailSenderImpl implements IEmailSender {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Verify your email!");
            helper.setFrom("kyakamhehamara@gamil.com");

            mailSender.send(mimeMessage);
        }
        catch (Exception ex) {
            log.error("Failed to send email: ", ex);
//            throw new MessagingException("Failed to send email: "+ ex.getMessage(), ex);
        }
    }
}