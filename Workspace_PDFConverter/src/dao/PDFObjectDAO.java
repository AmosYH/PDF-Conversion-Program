package dao;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

import util.DBManager;
import util.TextParser;
import util.Utility;
import object.PDFObject;
import setting.LogController;

public class PDFObjectDAO {
	
	public static ArrayList<PDFObject> retrieveReport(Connection conn) {
		LogController.writeMessage(LogController.DEBUG, "PDFConverter Start to retrieveReport fromDB");
		
		Statement stmt = null;
		ResultSet rs = null;

		ArrayList<PDFObject> reports = new ArrayList<PDFObject>();
		
		String selectSql = "SELECT * FROM [K2].[SmartBoxData].[DIS_SMO_PDF] where PDFPageCount is null "; //WMG
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(selectSql);

			LogController.writeMessage(LogController.DEBUG, "SQL: " + selectSql);
			
			while (rs.next()) {
				String IID = rs.getString("IID");
				String OriMessage=rs.getString("OriMessage");
				String OriFileType= rs.getString("OriFileType");
				
				int PID = rs.getInt("PID");
				String Project= rs.getString("Project");
				String OriFileName= rs.getString("OriFileName"); 
				String OriFilePath= rs.getString("OriFilePath");
				String TargetFilePath=rs.getString("TargetFilePath");
				String PDFFileName=rs.getString("PDFFileName");
				int PDFPageCount=rs.getInt("PDFPageCount");
				String PDFFilePath=rs.getString("PDFFilePath");
				String GenDateTime=rs.getString("GenDateTime");
				String PDFFileSize=rs.getString("PDFFileSize");
				
				PDFObject report = new PDFObject(PID, IID, Project, OriFileName, OriFileType, OriFilePath, OriMessage, 
						TargetFilePath, PDFFileName, PDFPageCount, PDFFilePath, GenDateTime, PDFFileSize);
				
				reports.add(report);
			}
			
		} catch (SQLException e) {			
			LogController.writeExceptionMessage(LogController.ERROR, e);
		} finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					LogController.writeExceptionMessage(LogController.ERROR, e);
				}
			}
			if(stmt!=null){
				try {
					stmt.close();
				} catch (SQLException e) {
					LogController.writeExceptionMessage(LogController.ERROR, e);
				}
			}
		}
		return reports;
	}
	
	public static int updateMsgAndPdf(Connection conn, PDFObject rpt, TextParser parser) {
		
		int result=-1;
		String pdfName = "";
		String pdfOriPath ="";
		String pdfDestPath ="";
		String OriFileName = "";
		PreparedStatement masterPreStmt = null;
		ResultSet rs = null;
		String updateMasterSQL = "UPDATE [K2].[SmartBoxData].[DIS_SMO_PDF] " //WMG
				+ "SET [PDFFileName]=?, "
				+ "[GenDateTime]=?, "
				+ "[PDFPageCount]=?, "
				+ "[PDFFileSize]=? "
				+ "WHERE [PID]=? and [IID]=?;";

		try {
			if("message".equalsIgnoreCase(rpt.getOriFileType())){
				LogController.writeMessage(LogController.DEBUG, "PDFConverter Start to update msg filetype");
				pdfName = "Message_"+ rpt.getPID()+"_" + rpt.getIID() + ".pdf";
				pdfDestPath = Utility.getProperty("destPath") + pdfName;
				parser.msg2pdf(rpt.getOriMessage(), pdfDestPath);
			} else if("pdf".equalsIgnoreCase(rpt.getOriFileType())){
				LogController.writeMessage(LogController.DEBUG, "PDFConverter Start to update pdf filetype");
				pdfOriPath = rpt.getOriFilePath();
				OriFileName = rpt.getOriFileName();
				pdfName = OriFileName;
				pdfDestPath = Utility.getProperty("destPath") + OriFileName;
				File source = new File(pdfOriPath);
				File target = new File(pdfDestPath);
				FileUtils.copyFile(source, target);
			}
			
			Timestamp ts = new Timestamp(new Date().getTime());
            int PDFPageCountValue = TextParser.getPDFPage(pdfDestPath);
			long fileSize = Math.round(TextParser.getFileSize(pdfDestPath)/1024.0);
			
			conn.setAutoCommit(false);
			masterPreStmt = conn.prepareStatement(updateMasterSQL);
			int i = 1;
			masterPreStmt.setString(i++, pdfName);
			masterPreStmt.setTimestamp(i++, ts);	
			masterPreStmt.setInt(i++, PDFPageCountValue);	
			masterPreStmt.setLong(i++, fileSize);
			masterPreStmt.setInt(i++, rpt.getPID());
			masterPreStmt.setString(i++, rpt.getIID());
			masterPreStmt.executeUpdate();
			conn.commit();
			conn.setAutoCommit(true);
			result = 0;
			
		} catch (Exception e) {
			LogController.writeExceptionMessage(LogController.ERROR, e);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					LogController.writeExceptionMessage(LogController.ERROR, e);
				}
			}
			if(masterPreStmt != null){
				try {
					masterPreStmt.close();
				} catch (SQLException e) {
					LogController.writeExceptionMessage(LogController.ERROR, e);
				}
			}
		}
		return result;
	}
	
	public static int ConvertToPdf(Connection conn, PDFObject rpt, TextParser parser) {

		int result = -1;
		String pdfName = "";
		String fileName = "";
		String file2pdf = "";
		String OriFileName = "";
		PreparedStatement masterPreStmt = null;
		ResultSet rs = null;
		String updateMasterSQL = "UPDATE [K2].[SmartBoxData].[DIS_SMO_PDF] " //WMG
				+ "SET [PDFFileName]=?, "
				+ "[GenDateTime]=?, "
				+ "[PDFPageCount]=?, "
				+ "[PDFFileSize]=? "
				+ "WHERE [PID]=? and [IID]=?;";
		
		try {	
			fileName = rpt.getOriFilePath();
			int suffix = rpt.getOriFileName().indexOf('.');
			OriFileName = rpt.getOriFileName().substring(0, suffix);
			pdfName = OriFileName + ".pdf";
			file2pdf = Utility.getProperty("destPath") + OriFileName + ".pdf";
			
			if("txt".equalsIgnoreCase(rpt.getOriFileType())) {
				LogController.writeMessage(LogController.DEBUG, "PDFConverter Start to update txt filetype");
				parser.text2pdf(fileName, file2pdf);
			} else if("prt".equalsIgnoreCase(rpt.getOriFileType())) {
				LogController.writeMessage(LogController.DEBUG, "PDFConverter Start to update prt filetype");
				parser.text2pdf(fileName, file2pdf);
			} else if("csv".equalsIgnoreCase(rpt.getOriFileType())) {
				LogController.writeMessage(LogController.DEBUG, "PDFConverter Start to update csv filetype");
				parser.csv2pdf(fileName, file2pdf);
			} else if("msg".equalsIgnoreCase(rpt.getOriFileType())) {
				LogController.writeMessage(LogController.DEBUG, "PDFConverter Start to update msg filetype");
				parser.email2pdf(fileName, file2pdf);
			} else if("tif".equalsIgnoreCase(rpt.getOriFileType()) || "tiff".equalsIgnoreCase(rpt.getOriFileType())) {
				LogController.writeMessage(LogController.DEBUG, "PDFConverter Start to update tif filetype");
				parser.tif2pdf(fileName, file2pdf);
			} else if("jpg".equalsIgnoreCase(rpt.getOriFileType()) || "jpeg".equalsIgnoreCase(rpt.getOriFileType())) {
				LogController.writeMessage(LogController.DEBUG, "PDFConverter Start to update jpg filetype");
				parser.jpg2pdf(fileName, file2pdf);
			}
			
			Timestamp ts = new Timestamp(new Date().getTime());
			int PDFPageCountValue = TextParser.getPDFPage(file2pdf);
			long fileSize = Math.round(TextParser.getFileSize(file2pdf)/1024.0);
			
			conn.setAutoCommit(false);
			masterPreStmt = conn.prepareStatement(updateMasterSQL);
			int i = 1;
			masterPreStmt.setString(i++, pdfName);
			masterPreStmt.setTimestamp(i++, ts);	
			masterPreStmt.setInt(i++, PDFPageCountValue);	
			masterPreStmt.setLong(i++, fileSize);
			masterPreStmt.setInt(i++, rpt.getPID());
			masterPreStmt.setString(i++, rpt.getIID());
			masterPreStmt.executeUpdate();
			conn.commit();
			conn.setAutoCommit(true);
			result = 0;
		} catch (Exception e) {
			LogController.writeExceptionMessage(LogController.ERROR, e);
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					LogController.writeExceptionMessage(LogController.ERROR, e);
				}
			}
			if(masterPreStmt!=null){
				try {
					masterPreStmt.close();
				} catch (SQLException e) {
					LogController.writeExceptionMessage(LogController.ERROR, e);
				}
			}
		}
		return result;
	}
}