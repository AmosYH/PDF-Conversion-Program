package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Locale;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.io.RandomAccessSource;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;


import com.opencsv.CSVReader;

import setting.LogController;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.hsmf.*;
import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import org.apache.commons.collections4.*;


public class TextParser {

	private static TextParser parser = null;
	private static String FONT = Utility.getProperty("FONT");
	
	private TextParser() {
	}

	public static synchronized TextParser getInstance() {
		if (parser == null) {
			parser = new TextParser();
		}
		
		return parser;
	}
	
	public void text2pdf(String text, String pdf) throws Exception {
		try {
			BaseFont bfChinese = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			Font FontChinese = new Font(bfChinese, 12, Font.NORMAL);
			
			Rectangle rect = new Rectangle(PageSize.A4.rotate());
			Document doc = new Document(rect);
			OutputStream out = new FileOutputStream(new File(pdf));
			PdfWriter.getInstance(doc, out);
			doc.open();
			
			InputStreamReader isr = new InputStreamReader(new FileInputStream(new File(text)), "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(isr);
			String str = "";
			
			while ((str = bufferedReader.readLine()) != null) {
				doc.add(new Paragraph(str, FontChinese));
			}
			
			bufferedReader.close();
			doc.close();
		} catch (DocumentException e) {
			LogController.writeExceptionMessage(LogController.ERROR, e);
		} catch (IOException e) {
			LogController.writeExceptionMessage(LogController.ERROR, e);
		}
	}
	
	public void csv2pdf(String csv, String pdf) throws Exception {
		try {
			CSVReader reader = new CSVReader(new FileReader(csv));
			String [] nextLine = reader.readNext();
			int columnCount = nextLine.length;

			Document doc = new Document(PageSize.A4.rotate());
	        PdfWriter.getInstance(doc, new FileOutputStream(pdf));
	        doc.open();            
	        PdfPTable table = new PdfPTable(columnCount);
	        table.setWidthPercentage(100);
	        PdfPCell table_cell;
			
	        while (nextLine != null) {
	        	for (int i = 0;i < columnCount;i++) {
	        		table_cell=new PdfPCell(new Phrase(nextLine[i]));
	                table.addCell(table_cell);
	        	}
	            nextLine = reader.readNext();
	        }
	        
	        doc.add(table);                       
	        doc.close();  
	        reader.close();
		} catch (DocumentException e) {
			LogController.writeExceptionMessage(LogController.ERROR, e);
		} catch (IOException e) {
			LogController.writeExceptionMessage(LogController.ERROR, e);
		}
	}
	
	public void email2pdf(String email, String pdf) throws Exception {
		try {	
			BaseFont bfChinese = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			Font FontChinese = new Font(bfChinese, 12, Font.NORMAL);
			
			MAPIMessage msg = new MAPIMessage(email);
			Document doc = new Document(PageSize.A4);
	        PdfWriter.getInstance(doc, new FileOutputStream(pdf));
	        doc.open();
	        Paragraph p = new Paragraph();
	        p.setFont(FontChinese);
			p.setAlignment(Element.ALIGN_JUSTIFIED);
        
        
        	p.add("From: " + msg.getDisplayFrom() + "\n");
        	p.add("To: " + msg.getDisplayTo() + "\n");
        	p.add("CC: " + msg.getDisplayCC() + "\n");
        	p.add("BCC: " + msg.getDisplayBCC() + "\n");
        	p.add("Subject: " + msg.getSubject() + "\n\n");
        	p.add(msg.getTextBody() + "\n");
        	
        	doc.add(p);
    		doc.close();
        } catch (ChunkNotFoundException e) {
        	LogController.writeExceptionMessage(LogController.ERROR, e);
        } catch (DocumentException e) {
			LogController.writeExceptionMessage(LogController.ERROR, e);
		} catch (IOException e) {
			LogController.writeExceptionMessage(LogController.ERROR, e);
		}
	}
	
	public void tif2pdf(String tif, String pdf) throws Exception {
		try{
	        RandomAccessFileOrArray myTiffFile=new RandomAccessFileOrArray(new RandomAccessSourceFactory().createBestSource(tif));
	        int numberOfPages=TiffImage.getNumberOfPages(myTiffFile);
	        Document TifftoPDF=new Document();
	        PdfWriter.getInstance(TifftoPDF, new FileOutputStream(pdf));
	        TifftoPDF.open();

	        for(int i = 1; i <= numberOfPages; i++){
	            Image tempImage=TiffImage.getTiffImage(myTiffFile, i);
	            Rectangle pageSize = new Rectangle(tempImage.getWidth(), tempImage.getHeight());
	            TifftoPDF.setPageSize(pageSize);
	            TifftoPDF.newPage();
	            //tempImage.scaleToFit(TifftoPDF.getPageSize());
	            TifftoPDF.add(tempImage);
	        }
	        TifftoPDF.close();
	    } catch (DocumentException e) {
			LogController.writeExceptionMessage(LogController.ERROR, e);
		} catch (IOException e) {
			LogController.writeExceptionMessage(LogController.ERROR, e);
		}
	}
	
	public void jpg2pdf(String jpg, String pdf) throws Exception {
		try{
		    Document doc=new Document();
		    PdfWriter.getInstance(doc, new FileOutputStream(pdf));
		    doc.open();
		    
		    Image convertJpg=Image.getInstance(jpg);

		    doc.setPageSize(convertJpg);
		    doc.newPage();
		    convertJpg.setAbsolutePosition(0, 0);
  
		    doc.add(convertJpg);
		    doc.close();
		} catch (DocumentException e) {
			LogController.writeExceptionMessage(LogController.ERROR, e);
		} catch (IOException e) {
			LogController.writeExceptionMessage(LogController.ERROR, e);
		}
	}
	
	public void msg2pdf(String msg, String pdf) throws Exception {
		try {
			BaseFont bfChinese = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			Font FontChinese = new Font(bfChinese, 12, Font.NORMAL);
			Rectangle rect = new Rectangle(PageSize.A4.rotate());

			FileOutputStream out = new FileOutputStream(pdf);
			Document doc = new Document(rect);
			PdfWriter writer = PdfWriter.getInstance(doc, out);

			doc.open();
			Paragraph p = new Paragraph();
			p.setFont(FontChinese);
			p.setAlignment(Element.ALIGN_JUSTIFIED);
			
			p.add(msg + "\n");
			doc.add(p);
			doc.close();
		} catch (DocumentException e) {
			LogController.writeExceptionMessage(LogController.ERROR, e);
		} catch (IOException e) {
			LogController.writeExceptionMessage(LogController.ERROR, e);
		}
	}

    public static long getFileSize(String filename) {
        File file = new File(filename);
        if (!file.exists() || !file.isFile()) {
            System.out.println("file not exist");
            return -1;
        }
        return file.length();
    }
	
    public static int getPDFPage(String pdfPath){
    	int pages = 0;
        File file = new File(pdfPath);
        PdfReader pdfReader = null;
        
		try {
			pdfReader = new PdfReader(new FileInputStream(file));
	        pages = pdfReader.getNumberOfPages();
	        System.out.println("pdf pages:" + pages);
		}catch(FileNotFoundException e){
			LogController.writeExceptionMessage(LogController.ERROR, e);
		}catch (IOException e) {
			LogController.writeExceptionMessage(LogController.ERROR, e);
		}finally{
			pdfReader.close();
		}
        return pages;
    }
	
    private static File generatePdfFile(File file,String dest) throws IOException, DocumentException {
        String fileName = file.getName();
        String pdfFileName = dest+"/"+fileName.substring(0, fileName.lastIndexOf(".")) + ".pdf";
        
        Document doc = new Document(PageSize.A4, 20, 20, 20, 20);
        PdfWriter.getInstance(doc, new FileOutputStream(pdfFileName));
        doc.open();
        doc.newPage();
        Image image = Image.getInstance(file.getPath());
        float height = image.getHeight();
        float width = image.getWidth();
        int percent = getPercent(height, width);
        image.setAlignment(Image.MIDDLE);
        image.scalePercent(percent);
        doc.add(image);
        doc.close();
        File pdfFile = new File(pdfFileName);
        return pdfFile;
    }
  
    private static int getPercent(float height, float weight) {
        float percent = 0.0F;
        if (height > weight) {
            percent = PageSize.A4.getHeight() / height * 100;
        } else {
            percent = PageSize.A4.getWidth() / weight * 100;
        }
        return Math.round(percent);
    }
}
