package icn.premierandroid.misc;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Determines a connection to SMTP server in GMail, when user submits a ScoutMeFragment Form.
 */

class GMail {

    private String fromEmail;
    private String fromPassword;
    @SuppressWarnings("rawtypes")
    private String toEmailList;
    private String emailSubject;
    private String emailBody;
    private String head_image;
    private String full_image;

    private Properties emailProperties;
    private Session mailSession;
    private MimeMessage emailMessage;
    private boolean isDiscounted;

    @SuppressWarnings("rawtypes")
    GMail(String fromEmail, String fromPassword,
          String toEmailList, String emailSubject, String emailBody, String head_image, String full_image, boolean isDiscounted) {
        this.fromEmail = fromEmail;
        this.fromPassword = fromPassword;
        this.toEmailList = toEmailList;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;
        this.head_image = head_image;
        this.full_image = full_image;

        emailProperties = System.getProperties();
        String emailHost = "smtp.gmail.com";
        emailProperties.put("mail.smtp.host", emailHost);
        String emailPort = "587";
        emailProperties.put("mail.smtp.port", emailPort);
        String smtpAuth = "true";
        emailProperties.put("mail.smtp.auth", smtpAuth);
        String starttls = "true";
        emailProperties.put("mail.smtp.starttls.enable", starttls);
        Log.i("GMail", "Mail server properties set.");
    }

    MimeMessage createEmailMessage() throws
            MessagingException, UnsupportedEncodingException {
        mailSession = Session.getInstance(emailProperties, null);
        mailSession.setDebug(false);
        emailMessage = new MimeMessage(mailSession);
        emailMessage.setFrom(new InternetAddress(fromEmail, fromEmail));
        Log.i("GMail","toEmail: "+toEmailList);
        emailMessage.addRecipient(Message.RecipientType.TO,
                new InternetAddress(toEmailList));
        emailMessage.setSubject(emailSubject);
        MimeMultipart multipart;
        if (isDiscounted) {
            multipart = new MimeMultipart();
            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setContent(emailBody, "text/html");
            multipart.addBodyPart(mbp1);
            addAttachment(multipart, head_image);
            addAttachment(multipart, full_image);
        } else {
            multipart = new MimeMultipart();
            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setContent(emailBody, "text/html");
            multipart.addBodyPart(mbp1);
        }
        emailMessage.setContent(multipart);
        Log.i("GMail", "Email Message created.");
        return emailMessage;
    }

    void sendEmail() throws MessagingException {
        Transport transport = mailSession.getTransport("smtp");
        String emailHost = "smtp.gmail.com";
        transport.connect(emailHost, fromEmail, fromPassword);
        Log.i("GMail","allrecipients: "+ Arrays.toString(emailMessage.getAllRecipients()));
        transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
        transport.close();
        Log.i("GMail", "Email sent successfully.");

    }

    private static void addAttachment(Multipart multipart, String filename) throws MessagingException {
        DataSource source = new FileDataSource(filename);
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        multipart.addBodyPart(messageBodyPart);
    }


}