package com.leanx.app.service.modules.system;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Service class responsible for sending emails
 */
public class EmailService {

    private static final Logger logger = Logger.getLogger(EmailService.class.getName());

    private final String smtpHost = System.getenv("SMTP_HOST");
    private final int smtpPort = 587;
    private final String smtpUsername = System.getenv("SMTP_USERNAME");
    private final String smtpPassword = System.getenv("SMTP_PASSWORD");

    private static final int MAX_RETRIES = 3; // Maximum number of retry attempts
    private static final long BACKOFF_DELAY = 60000; // 1 minute in milliseconds

    /**
     * Attempts to send an email containing login credentials to a new user.
     * This method implements a retry mechanism to handle potential transient
     * issues with the email service. It will attempt to send the email up to
     * {@link #MAX_RETRIES} times, with a delay of {@link #BACKOFF_DELAY}
     * milliseconds between retries.
     *
     * @param to       The recipient's email address.
     * @param username The username of the new account.
     * @param password The temporary password for the new account.
     * @return 0 if the email was sent successfully, -1 if all retry attempts failed
     * or if the retry process was interrupted.
     */
    public int attemptSendCredentialsEmail(String to, String username, String password) {
        int retryCount = 0;

        while (retryCount < MAX_RETRIES) {
            try {
                sendCredentialsEmail(to, username, password);
                logger.log(Level.INFO, "Email successfully sent to {0} after {1} retries.", new Object[]{to, retryCount});
                return 0; // Success
            } catch (MessagingException e) {
                retryCount++;
                logger.log(Level.SEVERE, "Attempt {0} - Failed to send email to {1}: {2}", new Object[]{retryCount, to, e.getMessage(), e});

                if (retryCount < MAX_RETRIES) {
                    if (!sleepBeforeRetry()) {
                        return -1; // Interrupted, exit early
                    }
                } else {
                    logger.log(Level.SEVERE, "Giving up after {0} attempts to send email to {1}.", new Object[]{MAX_RETRIES, to});
                    return -1;
                }
            }
        }
        return -1;
    }

    /**
     * Pauses the current thread for a predefined duration before attempting
     * to retry sending the email.
     *
     * @return {@code true} if the thread slept without interruption, {@code false}
     * if the sleep was interrupted.
     */
    private boolean sleepBeforeRetry() {
        try {
            logger.log(Level.WARNING, "Retrying in {0} milliseconds...", BACKOFF_DELAY);
            Thread.sleep(BACKOFF_DELAY);
            return true;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            logger.log(Level.WARNING, "Retry sleep interrupted. Aborting retries.");
            return false;
        }
    }

    /**
     * Sends an email containing the new user's login credentials to the specified
     * email address. The email includes the username, temporary password, and a
     * link to the LeanX login page. The email is formatted as HTML.
     *
     * @param to       The recipient's email address.
     * @param username The username of the new account.
     * @param password The temporary password for the new account.
     * @throws MessagingException If an error occurs while preparing or sending the email.
     */
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

            Transport.send(message);
        } catch (MessagingException e) {
            throw e;
        }
    }
}