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

    private final String smtpHost = System.getenv("SMTP_HOST");
    private final int smtpPort = 587;
    private final String smtpUsername = System.getenv("SMTP_USERNAME");
    private final String smtpPassword = System.getenv("SMTP_PASSWORD");

    private static final int MAX_RETRIES = 3; // Maximum number of retry attempts
    private static final long BACKOFF_DELAY = 60000; // 1 minute in milliseconds

    public int attemptSendCredentialsEmail(String to, String username, String password) {
        int retryCount = 0;

        while (retryCount < MAX_RETRIES) {
            try {
                sendCredentialsEmail(to, username, password);
                logger.log(Level.INFO, "Email successfully sent to: {0} after {1} retries.", new Object[]{to, retryCount});
                return 0; // Email sent successfully, exit the retry loop
            } catch (MessagingException e) {
                logger.log(Level.SEVERE, "Failed to send email to " + to + ", attempt " + (retryCount + 1) + ": " + e.getMessage(), e);
                retryCount++;

                if (retryCount < MAX_RETRIES) {
                    logger.log(Level.WARNING, "Retrying email to {0} in {1} milliseconds...", new Object[]{to, BACKOFF_DELAY});
                    try {
                        Thread.sleep(BACKOFF_DELAY);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        logger.log(Level.WARNING, "Retry sleep interrupted.");
                        return -1;
                    }
                } else {
                    logger.log(Level.SEVERE, "Failed to send email to {0} after {1} retries. Giving up.", new Object[]{to, MAX_RETRIES});
                    return -1;
                }
            }
        }

        return -1;
    }

    private void sendCredentialsEmail(String to, String username, String password) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", String.valueOf(smtpPort));
        props.put("mail.debug", "true"); // Enable JavaMail Debugging

        try {
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpUsername, smtpPassword);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(smtpUsername));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("NO-REPLY: Dein neuer LeanX Account");
            String messageBody = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                <meta charset="UTF-8">
                <title>Dein neuer LeanX Account</title>
                </head>
                <body>
                    <p style="color:red;"><b>ACHTUNG!</b> Dies ist eine automatisch generierte E-Mail. Bitte antworten Sie nicht hierauf!</p>
                    <br>
                    <p>Willkommen im Team!</p>
                    <p>Hier sind Ihre Zugangsdaten für <b>LeanX</b>, unser selbst entwickletes lightweight ERP-System:</p>
                    <ul>
                        <li>Login: <a href="www.lean-x.de">www.lean-x.de</a></li>
                    </ul>
                    <p>Ihre Zugangsdaten:</p>
                    <ul>
                        <li><b>Benutzername:</b> %s</li>
                        <li><b>Passwort:</b> %s</li>
                    </ul>
                    <p><b>WICHTIG:</b> Das Passwort läuft in <b>5 Tagen</b> ab! Bitte ändern Sie es rechtzeitig.</p>
                    <p>Bei Fragen oder Problemen wenden Sie sich bitte direkt an den IT-Support: it.support@lean-x.de</p>
                    <br>
                    <p>Beste Grüße<br>Dein IT-Service-Team<br><b>SalesUP GmbH IT</b></p>
                </body>
                </html>
                """, username, password);

            // Set the content of the message as HTML
            message.setContent(messageBody, "text/html; charset=utf-8");

            jakarta.mail.Transport.send(message);
        } catch (MessagingException e) {
            throw e;
        }
    }
}
