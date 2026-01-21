package com.testapp.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

@ApplicationScoped
public class MailService {

    private static final String SMTP_HOST = "smtp.example.com";        // ex: smtp.gmail.com
    private static final int SMTP_PORT = 587;
    private static final String SMTP_USERNAME = "ziadblhmr6@gmail.com";
    private static final String SMTP_PASSWORD = "xcgazlimwhcwudgf";

    

    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));
        props.put("mail.smtp.starttls.enable", "true");

        return Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });
    }

    public void sendMail(String to, String subject, String text) throws MessagingException {
        // nettoyer l'adresse
        if (to != null) {
            to = to.trim();
            // supprimer crochets éventuels
            to = to.replace("[", "").replace("]", "");
        }

        Session session = createSession();

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SMTP_USERNAME));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject, "UTF-8");
        message.setText(text, "UTF-8");

        Transport.send(message);
    }


    // 1) Mail au candidat après inscription
    public void sendRegistrationMailToCandidate(String candidateEmail,
    		String nom , String prenom) throws MessagingException {
        String subject = "Confirmation de votre inscription";
        String body =
                "Bonjour "+ nom +" "+ prenom +",\n\n" +
                "Votre inscription sur TestApp a bien été enregistrée.\n" +
                "Votre compte sera activé après validation par un administrateur.\n\n" +
                "Cordialement,\nL'équipe TestApp";

        sendMail(candidateEmail, subject, body);
    }

    // 2) Mail à l’admin pour validation
    public void sendAccountValidatedMailToCandidate(String candidateEmail,
            String nom,
            String prenom) throws MessagingException {
String subject = "Votre compte a été validé";
String body =
"Bonjour " + nom + " " + prenom + ",\n\n" +
"Votre compte sur TestApp a été validé par l'administrateur.\n" +
"Vous pouvez maintenant vous connecter et passer les tests.\n\n" +
"Cordialement,\nL'équipe TestApp";

sendMail(candidateEmail, subject, body);
}

}
