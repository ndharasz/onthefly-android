package com.example.noah.onthefly.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;
import java.util.Properties;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by ndharasz on 2/5/2017.
 */

public class Mailer extends javax.mail.Authenticator{
    private final String user = "";
    private final String pass = "";
    private final boolean auth = true;
    private String host;
    private String port;
    private String sport;
    private String from;
    private String to;
    private String subject;
    private String body;
    private Multipart multipart;

    public Mailer() {
        host = "smtp.gmail.com";
        port = "465";
        sport = "465";
        from = "ontheflyapp@android.com";
        subject = "[NO REPLY]";
        body = "Test";
        multipart = new MimeMultipart();

        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, pass);
    }

    public boolean send() throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.socketFactory.port", sport);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.user", user);
        props.setProperty("mail.smtp.password", pass);
        props.setProperty("mail.smtp.auth", Boolean.toString(auth));
        props.setProperty("debuggable", "true");

        if(to.length() > 0 && !from.equals("") && !subject.equals("") && !body.equals("")) {
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, pass);
                }
            });

            final Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject);
            msg.setSentDate(new Date());

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);
            multipart.addBodyPart(messageBodyPart);
            msg.setContent(multipart);

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Address[] recipient = {new InternetAddress(to)};
                        Transport.send(msg, recipient);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //throw this so that activity can catch a failure
                        throw new RuntimeException("Could not send.");
                    }
                }
            });
            t.start();
            return true;
        } else {
            return false;
        }
    }

    public void addAttachment(String filename) throws Exception {
//        BodyPart messageBodyPart = new MimeBodyPart();
//        DataSource source = new Data;
//        messageBodyPart.setDataHandler(new DataHandler(source));
//        messageBodyPart.setFileName(filename);
//
//        multipart.addBodyPart(messageBodyPart);
    }

    public static boolean isEmailValid(String email){
        //pay no attention to the regex behind the email validation
        String expression = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+"
                +"(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\""
                +"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]"
                +"|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")"
                +"@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+"
                +"[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.)"
                +"{3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:"
                +"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\"
                +"[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches() && !email.equals("")) {
            return true;
        }
        return false;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
