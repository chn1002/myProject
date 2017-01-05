package com.chn.relayservice;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
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

import com.chn.emailmain.grobalConfig;

public class emailService {
	private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	private Session mSession;


	//	public static void main(String args[]) throws Exception {
	//		new emailService().sendSSLMessage(sendTo, emailSubjectTxt, emailMsgTxt, emailFromAddress);
	//		System.out.println("Sucessfully Sent mail to All Users");
	//	}
	
	public emailService(final grobalConfig GroConf) {
		Properties props = new Properties();
		props.put("mail.smtp.host", GroConf.getEmailServer());
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "true");
		props.put("mail.smtp.port", GroConf.getEmailServerPort());
		props.put("mail.smtp.socketFactory.port", GroConf.getEmailServerPort());
		props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.put("mail.smtp.socketFactory.fallback", "false");

		mSession = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {

			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(GroConf.getEmailUser(),
						GroConf.getEmailPassword());
			}
		});
		
		mSession.setDebug(true);
	}

	public void sendSSLMessage(String recipients[], String subject,
			String message, String fileLocation, String from) throws MessagingException {

		Message msg = new MimeMessage(mSession);
		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);

		InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Setting the Subject and Content Type
		msg.setSubject(subject);

		// Create the message part
		BodyPart messageBodyPart = new MimeBodyPart();

		// Fill the message
		messageBodyPart.setText(message);
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		// Part two is attachment
		messageBodyPart = new MimeBodyPart();
		File file = new File(fileLocation);
		FileDataSource fds = new FileDataSource(file);
		messageBodyPart.setDataHandler(new DataHandler(fds));

		String fileName = fds.getName(); // 한글파일명은 영문으로 인코딩해야 첨부가 된다.
		try {
			fileName = new String(fileName.getBytes("KSC5601"), "8859_1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		messageBodyPart.setFileName(fileName);

		multipart.addBodyPart(messageBodyPart);

		// Put parts in message
		msg.setContent(multipart);

		// Send the message
		Transport.send(msg);

		System.out.println("E-mail successfully sent!!"); 
	}
}
