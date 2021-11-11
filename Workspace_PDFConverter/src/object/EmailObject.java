package object;

import javax.activation.DataSource;

import setting.EmailUtil;
import setting.LogController;
import util.CommonUtil;

public class EmailObject {
	
	private String mailFormat; 
	private String sender;
	private String mailTo;
	private String mailCc;
	private String mailBcc;
	private String subject;
	private StringBuffer sbText; 
	private DataSource[] dsArr; 
	private String[] attachmentNameArr; 
	private boolean[] inlineAttachment;
	private Integer retryNum;
	
	public String getMailFormat() {
		return (mailFormat == null || "".equals(mailFormat))? EmailUtil.Format_Html : mailFormat;
	}
	public String getSender() {
		return (sender == null || "".equals(sender))? CommonUtil.getProperty(EmailUtil.getEmailProperties(), "senderName", "") : sender;
	}
	public String getMailTo() {
		return (mailTo == null || "".equals(mailTo))? CommonUtil.getProperty(EmailUtil.getEmailProperties(), "mailTo", "") : mailTo;
	}
	public String getMailCc() {
		return (mailCc == null || "".equals(mailCc))? "" : mailCc;
	}
	public String getMailBcc() {
		return (mailBcc == null || "".equals(mailBcc))? "" :mailBcc;
	}
	public String getSubject() {
		return (subject == null || "".equals(subject))? CommonUtil.getProperty(EmailUtil.getEmailProperties(), "subject", "") : subject;
	}
	public StringBuffer getSbText() {
		return (sbText == null || "".equals(sbText))? getEmailBufferWithServerName() : sbText;
	}
	public DataSource[] getDsArr() {
		return dsArr;
	}
	public String[] getAttachmentNameArr() {
		return attachmentNameArr;
	}
	public int getRetryNum() {
		return (retryNum == null || "".equals(retryNum))? Integer.parseInt(CommonUtil.getProperty(EmailUtil.getEmailProperties(), "retryNum", "")) : retryNum;
	}
	public boolean[] getInlineAttachment() {
		return (inlineAttachment == null || "".equals(inlineAttachment) || inlineAttachment.length < 0)? null : inlineAttachment;
	}
	public void setMailFormat(String mailFormat) {
		this.mailFormat = mailFormat;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}
	public void setMailCc(String mailCc) {
		this.mailCc = mailCc;
	}
	public void setMailBcc(String mailBcc) {
		this.mailBcc = mailBcc;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public void setSbText(StringBuffer sbText) {
		this.sbText = sbText;
	}
	public void setDsArr(DataSource[] dsArr) {
		this.dsArr = dsArr;
	}
	public void setAttachmentNameArr(String[] attachmentNameArr) {
		this.attachmentNameArr = attachmentNameArr;
	}
	public void setInlineAttachment(boolean[] inlineAttachment) {
		this.inlineAttachment = inlineAttachment;
	}
	public void setRetryNum(int retryNum) {
		this.retryNum = retryNum;
	}
	
	//Email
    private static StringBuffer getEmailBufferWithServerName() {
        final StringBuffer sbText = new StringBuffer("<div style='width:100%; font-family: Times New Roman; font-size:14pt;'><u><b>");
        sbText.append("Server Information");
        sbText.append("</b></u><br/>");
        sbText.append("<div style='font-family: Times New Roman; font-size:12pt;'>" + CommonUtil.getProperty(EmailUtil.getEmailProperties(), "server_name", "") + "</div>");
        sbText.append("</div>");
        return sbText;
    }
}
