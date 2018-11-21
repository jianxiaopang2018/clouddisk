package top.jianxiaopang.clouddisk.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Mail {
	private static Session mailSession;
	private static Properties props;
	private static Transport transport;

	static {
		try {
			InputStream in = Mail.class.getClassLoader().getResourceAsStream("resources.properties");
			props = new Properties();
			props.load(in);
			mailSession = Session.getDefaultInstance(props);
			transport = mailSession.getTransport(props.getProperty("mail.transport.protocol"));
			transport.connect(props.getProperty("mail.smtp.host"),
					props.getProperty("username"), props.getProperty("password"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public static boolean sendMail(String email, String verificationCode){
		try {
			if(transport==null) {
				transport = mailSession.getTransport(props.getProperty("mail.transport.protocol"));
				transport.connect(props.getProperty("mail.smtp.host"),
						props.getProperty("username"), props.getProperty("password"));
			}
			Message msg = new MimeMessage(mailSession);
			msg.setFrom(new InternetAddress(props.getProperty("username")));
			msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			msg.setSubject("激活邮件");
			msg.setContent("<h1>请点击下面链接完成激活操作！</h1><h3><a href='https://www.jianxiaopang.top/clouddisk/user/activate?verificationCode="+verificationCode+"'>请点击这里激活！</a></h3>","text/html;charset=UTF-8");
			msg.saveChanges();
			transport.sendMessage(msg, msg.getAllRecipients());
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println(e);
			try {
				transport.close();
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
			return false;
		}
		return true;
	}
}
