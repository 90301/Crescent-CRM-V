package integrations;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import javax.activation.DataHandler;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GoogleMail extends HttpServlet{

	@Override
	  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    String type = req.getParameter("type");   
	      resp.getWriter().print("Sending simple email.");
	      sendMail();
	  }
	
	public static void sendMail(){
		Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);
	    
	    try {
	    	Message msg = new MimeMessage(session);
	    	  msg.setFrom(new InternetAddress("tosimmons18@gmail.com", "Example.com Admin"));
	    	  msg.addRecipient(Message.RecipientType.TO,
	    	                   new InternetAddress("tsiking18@gmail.com", "Mr. User"));
	    	  msg.setSubject("Your Example.com account has been activated");
	    	  msg.setText("This is test text");
	    	  Transport.send(msg);
	      } catch (AddressException e) {
	        e.printStackTrace();
	      } catch (MessagingException e) {
	        e.printStackTrace();
	      } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	      }
	}
}
