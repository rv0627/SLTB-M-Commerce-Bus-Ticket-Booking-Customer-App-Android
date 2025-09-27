package lk.jiat.sltb;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "EmailSender";

    private String senderEmail; // Your email address
    private String senderPassword; // Your email password
    private String recipientEmail; // Recipient's email address.
    private String subject; // Email subject
    private String body; // Email body

    public EmailSender(String senderEmail, String senderPassword, String recipientEmail, String subject, String body) {
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.body = body;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            // Configure mail server properties
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com"); // Use Gmail SMTP server
            props.put("mail.smtp.port", "587");

            // Create a session with authentication
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            // Create the email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(body);

            // Send the email
            Transport.send(message);
            Log.d(TAG, "Email sent successfully to " + recipientEmail);
            return true; // Success
        } catch (MessagingException e) {
            Log.e(TAG, "Failed to send email: " + e.getMessage());
            return false; // Failure
        }
    }
}