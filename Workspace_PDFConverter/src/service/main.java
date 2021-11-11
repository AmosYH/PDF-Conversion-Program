package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import dao.PDFObjectDAO;
import object.EmailObject;
import object.PDFObject;
import setting.EmailUtil;
import setting.LogController;
import util.CommonUtil;
import util.DBManager;
import util.TextParser;
import util.Utility;

import com.itextpdf.text.Image;

public class main {
	
	static int i = 0;
	static int j = 0;
	static int k = 0;
	static int l = 0;
	static int m = 0;
	static int n = 0;
	static int o = 0;
	static int p = 0;
	
//	static {
//		LogController.createLogFile();
//	}
	
	public static void main(String[] args){
		
		sendNotifyEmail("Start");
		LogController.writeMessage(LogController.DEBUG, "Start PDF Conversion program");
		LogController.writeMessage(LogController.DEBUG, "Start time: " + new Timestamp(new Date().getTime()));

		Connection conn = null;
		
		TextParser parser = TextParser.getInstance();
		
		try{
			//Locate target lists path
			conn = DBManager.makeConnection();
			conn.setAutoCommit(false);
			
			ArrayList<PDFObject> reports = null;
			reports = PDFObjectDAO.retrieveReport(conn);
			
			for (PDFObject report : reports) {
				String OriFileType = report.getOriFileType();
				
				if ("message".equalsIgnoreCase(OriFileType)) { 
					i++;
					PDFObjectDAO.updateMsgAndPdf(conn,report,parser);
				}
				else if ("pdf".equalsIgnoreCase(OriFileType)) {
					j++;
					PDFObjectDAO.updateMsgAndPdf(conn,report, parser);
				}
				else if ("txt".equalsIgnoreCase(OriFileType)) { 
					k++;
					PDFObjectDAO.ConvertToPdf(conn, report, parser);
				}
				else if ("msg".equalsIgnoreCase(OriFileType)) {
					l++;
					PDFObjectDAO.ConvertToPdf(conn, report, parser);
				}
				else if ("csv".equalsIgnoreCase(OriFileType)) {
					m++;
					PDFObjectDAO.ConvertToPdf(conn, report, parser);
				}
				else if ("prt".equalsIgnoreCase(OriFileType)) {
					n++;
					PDFObjectDAO.ConvertToPdf(conn, report, parser);
				}
				else if ("tif".equalsIgnoreCase(OriFileType) || "tiff".equalsIgnoreCase(OriFileType)) {
					o++;
					PDFObjectDAO.ConvertToPdf(conn, report, parser);
				}
				else if ("jpg".equalsIgnoreCase(OriFileType) || "jpeg".equalsIgnoreCase(OriFileType)) {
					p++;
					PDFObjectDAO.ConvertToPdf(conn, report, parser);
				}
			}

			LogController.writeMessage(LogController.DEBUG, "Number of Message handled: " + Integer.toString(i));
			LogController.writeMessage(LogController.DEBUG, "Number of PDF handled: " + Integer.toString(j));
			LogController.writeMessage(LogController.DEBUG, "Number of Text handled: " + Integer.toString(k));
			LogController.writeMessage(LogController.DEBUG, "Number of Msg handled: " + Integer.toString(l));
			LogController.writeMessage(LogController.DEBUG, "Number of Csv handled: " + Integer.toString(m));
			LogController.writeMessage(LogController.DEBUG, "Number of Prt handled: " + Integer.toString(n));
			LogController.writeMessage(LogController.DEBUG, "Number of Tif handled: " + Integer.toString(o));
			LogController.writeMessage(LogController.DEBUG, "Number of Jpg handled: " + Integer.toString(p));
	
		} catch(Exception e){
			LogController.writeMessage(LogController.ERROR, e.getMessage());
			LogController.writeExceptionMessage(LogController.DEBUG, e);
		} finally{
			if (conn!=null){
				try {
					conn.commit();
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					LogController.writeMessage(LogController.ERROR, e.getMessage());
					LogController.writeExceptionMessage(LogController.DEBUG, e);
				}
				DBManager.closeConnection(conn);
			}
		}
		
		sendNotifyEmail("End");
		LogController.writeMessage(LogController.DEBUG, "Finish PDF Conversion program");
		LogController.writeMessage(LogController.DEBUG, "End time: " + new Timestamp(new Date().getTime()));
	}

	private static void sendNotifyEmail(String status){
		EmailObject email  = new EmailObject();
		String subject = email.getSubject();
		if("Start".equals(status))
			subject += " Start";
		else {
			subject += " End";
			StringBuffer sBuffer = new StringBuffer("Number of Message handled: " + Integer.toString(i));
			sBuffer.append("\nNumber of PDF handled: " + Integer.toString(j));
			sBuffer.append("\nNumber of Text handled: " + Integer.toString(k));
			sBuffer.append("\nNumber of Msg handled: " + Integer.toString(l));
			sBuffer.append("\nNumber of Csv handled: " + Integer.toString(m));
			sBuffer.append("\nNumber of Prt handled: " + Integer.toString(n));
			sBuffer.append("\nNumber of Tif handled: " + Integer.toString(o));
			sBuffer.append("\nNumber of Jpg handled: " + Integer.toString(p));
			int sum = i + j + k + l + m + n + o + p;
			sBuffer.append("\nTotal Number of Files handled: " + Integer.toString(sum));
			email.setSbText(email.getSbText().append(sBuffer));
		}
		email.setSubject(subject);
		EmailUtil.sendEmail(email);
	}
	
}