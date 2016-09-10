package com.example.nalini.smstest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.widget.Toast;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.nalini.smstest.Mail;

import java.util.Properties;

public class MailTest extends BroadcastReceiver {
    private SmsMessage currentSMS;
    private String message;
    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdu_Objects = (Object[]) bundle.get("pdus");
                if (pdu_Objects != null) {

                    for (Object aObject : pdu_Objects) {

                        currentSMS = getIncomingMessage(aObject, bundle);

                        String senderNo = currentSMS.getDisplayOriginatingAddress();

                        message = currentSMS.getDisplayMessageBody();

//                        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(senderNo));
//                        Cursor c = context.getContentResolver().query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME},null,null,null);
//                        try {
//                            c.moveToFirst();
//                            String  displayName = c.getString(0);
//                            String ContactName = displayName;
//                            Toast.makeText(context, ContactName, Toast.LENGTH_LONG).show();
//                        } catch (Exception e) {
//                            // TODO: handle exception
//                            break;
//                        }

                        Toast.makeText(context,"after send............",Toast.LENGTH_LONG).show();
                        Toast.makeText(context, "senderNum: " + senderNo + " :\n message: " + message, Toast.LENGTH_LONG).show();



                DBHelper b = new DBHelper(context);
                int x =  b.numberOfRows();
                Toast.makeText(context, x+"", Toast.LENGTH_SHORT).show();
                Cursor resultSet = b.getAllData();
                resultSet.moveToLast();
                String username = resultSet.getString(1);
                String password = resultSet.getString(2);
                String receiver = resultSet.getString(3);
                Toast.makeText(context, username, Toast.LENGTH_SHORT).show();
                Mail m1 = new Mail();
                m1.setFrom(username);
                m1.setTo(receiver);
                m1.setPassword(password);
                m1.setBody(message);
                m1.setSubject(senderNo);
                Toast.makeText(context,m1.getFrom(),Toast.LENGTH_LONG).show();
                sendEmail(context,m1);
                    }
                }
            }
        }
    }

    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSMS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
        }
        return currentSMS;
    }

    public void sendEmail(Context context,Mail m){

        final String username = m.getFrom();
        final String password = m.getPassword();
       Toast.makeText(context, "Starting.", Toast.LENGTH_LONG).show();
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            //Creating a new session
            Session session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        //Authenticating the password
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(m.getFrom()));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(m.getFrom()));
            message.setSubject(m.getSubject());
            message.setText(m.getBody());
            Multipart multipart = new MimeMultipart("related");
            BodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent("<html>"+m.getBody()+"</html>", "text/html");

            multipart.addBodyPart(htmlPart);
            message.setContent(multipart);
            Transport.send(message);
            Toast.makeText(context, "Sent mail.", Toast.LENGTH_LONG).show();

        } catch(Exception e) {
            // some other problem  // some other problem
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }

    }
}