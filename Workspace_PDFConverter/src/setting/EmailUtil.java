package setting;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import object.EmailObject;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

import util.CommonUtil;

public class EmailUtil{
	public static final String Format_Text = "Text";
	public static final String Format_Html = "HTML";
	private static Properties emailProperties = new Properties();
	private static String smtpHost = "";
	private static int smtpPort = 25;
	private static String smtpUser = "";
	private static String smtpPasswd = "";
	private static String emailPrefix="";
	private static String mailFrom = "";
	private static String authorisedDomain = "";
	private static String [] authorisedDomainArray = new String[]{};
	private static boolean authorisedDomainEmpty=true;
	
	private static boolean debug = false;
	
	static{
		try{
			//K2
			File emailFile = new File("D:\\WMG_APP\\External_Program\\PDF_Convertor\\resource\\config\\email.properties"); //WMG
			
			emailProperties.load(new FileInputStream(emailFile));
			
			smtpHost = CommonUtil.getProperty(emailProperties, "smtpHost", "localhost");
			smtpPort = CommonUtil.getProperty(emailProperties, "smtpPort", 25);
			smtpUser = CommonUtil.getProperty(emailProperties, "smtpUser", "");
			smtpPasswd = CommonUtil.getProperty(emailProperties, "smtpPasswd", "");
			emailPrefix = CommonUtil.getProperty(emailProperties, "emailPrefix", "");
			
			authorisedDomain = CommonUtil.getProperty(emailProperties, "authorisedDomain", "");
			if (!"".equals(authorisedDomain)){
				authorisedDomainArray = authorisedDomain.split("\\,");
				for (int i=0;i<authorisedDomainArray.length;i++){
						authorisedDomainArray[i] = authorisedDomainArray[i].trim();
				}
				authorisedDomainEmpty = false;
			}
			
			mailFrom = CommonUtil.getProperty(emailProperties, "mailFrom", "admin@localhost");
			
			debug = CommonUtil.getProperty(emailProperties, "debug", false);
		} catch (Exception e){
			LogController.writeExceptionMessage(LogController.ERROR, e);
		}
	}
	
	/**
	 * @param mailFormat (HTML OR TEXT)
	 * @param mailTo - Separate each address by ,
	 * @param mailCc - Same as mailTo
	 * @param mailBcc - Same as mailTo
	 * @param subject - Email Subject
	 * @param sbText - Email Content
	 * @param dsArr - Array of dataSource of the attachment file
	 * @param attachmentNameArr - Array index match with ds
	 * @param retryNum
	 */	
	public static boolean sendEmail(EmailObject email){
		return sendEmail(email.getMailFormat(), email.getSender(), email.getMailTo(), email.getMailCc(), email.getMailBcc(), email.getSubject(), email.getSbText(), email.getDsArr(), email.getAttachmentNameArr(), email.getInlineAttachment(), email.getRetryNum(), true);
	}
	
	private static boolean sendEmail(String mailFormat, String senderName, String mailTo, String mailCc, String mailBcc, String subject, StringBuffer sbText, DataSource[] dsArr, String[] attachmentNameArr, boolean[] inlineAttachment, int retryNum, boolean isEmailError){
		if (dsArr != null && attachmentNameArr != null && inlineAttachment!=null){
			if (dsArr.length != attachmentNameArr.length){
				LogController.writeMessage(LogController.ERROR, "EmailUtil", "sendEmail", "Mismatch array size between dsArr & attachmentNameArr");
				return false;
			}else if (attachmentNameArr.length!=inlineAttachment.length){
				LogController.writeMessage(LogController.ERROR, "EmailUtil", "sendEmail", "Mismatch array size between inlineAttachment & attachmentNameArr");
				return false;
			}
		}else if ((dsArr == null && attachmentNameArr != null) || (dsArr != null && attachmentNameArr == null)){
			LogController.writeMessage(LogController.ERROR, "EmailUtil", "sendEmail", "Only one of the array (dsArr or attachmentNameArr) is NULL");
			return false;
		}
		
		Email email = null;
		if (authorisedDomain(mailTo, mailCc, mailBcc)){
			if(Format_Html.equals(mailFormat))
				email = new HtmlEmail();
			else if(Format_Text.equals(mailFormat)){
				if (dsArr != null && dsArr.length>0){
					email = new MultiPartEmail();
				}else{
					email = new SimpleEmail();
				}
			}else{
				LogController.writeMessage(LogController.ERROR, "EmailUtil", "sendEmail", "Wrong email format: " + mailFormat);
				return false;
			}
				
			//email configuration
			email.setDebug(debug);
			email.setHostName(smtpHost);
			email.setSmtpPort(smtpPort);
			
			if (smtpUser.length() > 0){
				email.setAuthentication(smtpUser, smtpPasswd);
			}
			
			email.setCharset("UTF-8");
						
			try{
				MimeMultipart content = null;
				MimeBodyPart htmlPart = null;
				if (dsArr != null && inlineAttachment!=null){					
					content = new MimeMultipart("related");
					htmlPart = new MimeBodyPart();
					htmlPart.setContent(sbText.toString(), "text/html;charset=UTF-8");
					content.addBodyPart(htmlPart);
				}else{
					email.setMsg(sbText.toString());
				}
				if(senderName==null||"".equals(senderName))
					email.setFrom(mailFrom);
				else
					email.setFrom(senderName);
				
				email.setSubject(("DEVCTM-TEST-FTWHPMU1".equals(subject))?subject:emailPrefix+subject);
				
				String [] mailToArray = mailTo.split("\\,");
				LogController.writeMessage(LogController.DEBUG, "mailToArray.length : " + mailToArray.length);
				for (int i = 0; i < mailToArray.length; i++){
					email.addTo(mailToArray[i].trim());
				}
				
				if(mailCc != null && (!"".equals(mailCc))){
					String [] mailCcArray = mailCc.split("\\,");
					for (int i = 0; i < mailCcArray.length; i++){
						email.addCc(mailCcArray[i].trim());
					}
				}
				
				if(mailBcc != null && (!"".equals(mailBcc))){
					String [] mailBccArray = mailBcc.split("\\,");
					for (int i = 0; i < mailBccArray.length; i++){
						email.addBcc(mailBccArray[i].trim());
					}
				}
				
				//set attachment
				if (dsArr != null){
					for (int i = 0 ; i < dsArr.length ; i++){
						MimeBodyPart messageBodyPart = new MimeBodyPart();
						messageBodyPart.setDataHandler(new DataHandler(dsArr[i]));
						if (inlineAttachment!=null && inlineAttachment[i]) {
							messageBodyPart.setContentID("<" + attachmentNameArr[i] + ">");
							messageBodyPart.setDisposition(MimeBodyPart.INLINE);
						} else {
							messageBodyPart.setFileName(attachmentNameArr[i]);				
						}
						
						if (content==null)
							content = new MimeMultipart();
						
						content.addBodyPart(messageBodyPart);
					}
					((MultiPartEmail) email).addPart(content);
				}

			}catch (EmailException e){
				if (isEmailError)
					LogController.writeExceptionMessage(LogController.DEBUG, e);
				else
					LogController.writeExceptionMessage(LogController.ERROR, e);
				return false;
			}catch (MessagingException e) {
				if (isEmailError)
					LogController.writeExceptionMessage(LogController.DEBUG, e);
				else
					LogController.writeExceptionMessage(LogController.ERROR, e);
				return false;
			}
			//end configuration	
						
			//send & retry mechanism
			for (int i = 0; i < retryNum; i++){
				try{
					email.send();
					return true;
				}catch(EmailException e){
					if (isEmailError)
						LogController.writeExceptionMessage(LogController.DEBUG, e);
					else
						LogController.writeExceptionMessage(LogController.ERROR, e);
				}
			}
		}
			
		return false;
	}
	
	/* For Check UAT email to public email address*/
	private static boolean authorisedDomain(String mailTo, String mailCc, String mailBcc){
		if (!authorisedDomainEmpty){
			if (isAuthorisedEmailList(mailTo) && isAuthorisedEmailList(mailCc) && isAuthorisedEmailList(mailBcc))
				return true;
			else
				return false;
		}
			
		return true;
	}
	
	private static boolean isAuthorisedEmailList(String mailList){
		if (mailList != null && !"".equals(mailList)){
			String [] mailToArray = mailList.split("\\,");
			for (int i = 0; i < mailToArray.length; i++){
				String mailToDomain = mailToArray[i].substring(mailToArray[i].indexOf('@')+1,mailToArray[i].length());
				boolean acceptDomain = false;
				for (int k=0; k<authorisedDomainArray.length; k++){
					if (mailToDomain.equalsIgnoreCase(authorisedDomainArray[k])){
						acceptDomain = true;
						break;
					}
				}
				
				if (!acceptDomain){
					LogController.writeMessage(LogController.ERROR, "EmailUtil", "isAuthorisedEmailList", "Email Blocked:"+mailToArray[i]);
					return false;
				}
			}
		}
		return true;
	}
	public static Properties getEmailProperties() {
		return emailProperties;
	}	
}