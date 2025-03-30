package com.jellyone.mail.services;

import com.jellyone.mail.errors.MailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;

@Service
@RequiredArgsConstructor
@Profile("mail")
public class MailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String username;

    private MimeMessageHelper createMessageHelper(MimeMessage mimeMessage, String to, String subject, Context context) throws MessagingException {
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        messageHelper.setFrom(username);
        messageHelper.setTo(to);
        messageHelper.setSubject("Отчет по мероприятию " + subject);
        String emailContent = templateEngine.process("more-beauty", context);
        messageHelper.setText(emailContent, true);
        return messageHelper;
    }

    public void sendSimpleMail(String to, String subject, Context context) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        createMessageHelper(mimeMessage, to, subject, context);
        mailSender.send(mimeMessage);
    }

    public void sendEmailWithAttachment(String to, String subject, File file, Context context) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = createMessageHelper(mimeMessage, to, subject, context);
        messageHelper.addAttachment(file.getName(), file);
        mailSender.send(mimeMessage);
    }

    public void sendMail(String email, String subject, Context context) {
        try {
            sendSimpleMail(email, subject, context);
        } catch (MailSendException ignored) {
        } catch (MessagingException e) {
            throw new MailException(e.getMessage());
        }
    }

    public void sendAttachMail(String email, String name, File file, Context context) {
        try {
            sendEmailWithAttachment(email, name, file, context);
        } catch (MailSendException ignored) {
        } catch (MessagingException e) {
            throw new MailException(e.getMessage());
        }
    }
}
