package icn.premierandroid.misc;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SmtpAuthenticator extends Authenticator {

    public SmtpAuthenticator() {

        super();
    }
    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        String fromEmail = "becomeamodelsender@gmail.com";
        String fromPassword = "Premier12";
        if ((fromEmail!= null) && (fromEmail.length() > 0) && (fromPassword != null)
                && (fromPassword.length() > 0)) {

            return new PasswordAuthentication(fromEmail, fromPassword);
        }

        return null;
    }
}