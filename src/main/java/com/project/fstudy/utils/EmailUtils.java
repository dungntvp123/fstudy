package com.project.fstudy.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailUtils {
    @Autowired
    private JavaMailSender sender;

    public void sendSimpleEmail(String to, String subject, String content) {
        log.info("(Send simple message)");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("tridungtin8@gmail.com");
        message.setTo(to);
        message.setText(content);
        message.setSubject(subject);
        sender.send(message);
    }

    public void sendAttachmentEmail(String to, String subject, String contentAsString) throws MessagingException
    {
        log.info("(send mime message)");
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("tridungtin8@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(contentAsString, true);
        sender.send(message);
    }
}
