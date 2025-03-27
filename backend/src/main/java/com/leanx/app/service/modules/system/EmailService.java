package com.leanx.app.service.modules.system;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {
    private static final Logger logger = Logger.getLogger(EmailService.class.getName());

    private final String smtpHost = "smtp.gmail.com"; // SMTP server
    private final int smtpPort = 587; // Use 465 for SSL, 587 for TLS
    private final String smtpUsername = System.getenv("SMTP_USERNAME");
    private final String smtpPassword = System.getenv("SMTP_PASSWORD");

    public void sendCredentialsEmail(String to, String username, String password) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", String.valueOf(smtpPort));

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpUsername, smtpPassword);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(smtpUsername));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Dein neuer LeanX Account");
            message.setText("""
                ACHTUNG! Dies ist eine automatisierte E-Mail. Bitte antworten Sie nicht hierauf!
                
                Willkommen im Team!
                
                Hier sind Ihre Zugangsdaten für LeanX, unser lightweight ERP-System:
                ➤ Login: http://16.16.234.230:80/login
                
                Ihre Zugangsdaten:
                • **Benutzername:** """ + username + """
                • **Passwort:** """ + password + """
                
                ⚠ **WICHTIG:** Ihr Passwort läuft in **5 Tagen** ab! Bitte ändern Sie es rechtzeitig.
                
                Bei Fragen oder Problemen wenden Sie sich bitte an den IT-Support.
                
                Beste Grüße  
                Ihr IT-Service-Team  
                **IT SalesUP GmbH**
                """);
            logger.log(Level.INFO, "Email successfully sent to: {0}", to);
        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "Failed to send email to " + to, e);
        }
    }
}
