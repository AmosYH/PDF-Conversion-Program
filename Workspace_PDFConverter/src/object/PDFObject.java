package object;

public class PDFObject {

	private int PID, PDFPageCount;
	private String IID, Project, OriFileName, OriFileType, OriFilePath, OriMessage, TargetFilePath, PDFFileName, 
	PDFFilePath, GenDateTime, PDFFileSize;

	public PDFObject(int PID, String IID, String Project, String OriFileName, String OriFileType, String OriFilePath,
			String OriMessage, String TargetFilePath, String PDFFileName, 
			int PDFPageCount, String PDFFilePath, String GenDateTime, String PDFFileSize) {
		this.PID = PID;
		this.IID = IID;
		this.Project = Project;
		this.OriFileName = OriFileName;
		this.OriFileType = OriFileType;
		this.OriFilePath = OriFilePath;
		this.OriMessage = OriMessage;
		this.TargetFilePath = TargetFilePath;
		this.PDFFileName = PDFFileName;
		this.PDFPageCount = PDFPageCount;
		this.PDFFilePath = PDFFilePath;
		this.GenDateTime = GenDateTime;
		this.PDFFileSize = PDFFileSize;
	}

	public int getPID() {
		return PID;
	}

	public void setPID(int PID) {
		this.PID = PID;
	}

	public String getIID() {
		return IID;
	}

	public void setIID(String IID) {
		this.IID = IID;
	}

	public String getProject() {
		return Project;
	}

	public void setProject(String Project) {
		this.Project = Project;
	}

	public String getOriFileName() {
		return OriFileName;
	}

	public void setOriFileName(String OriFileName) {
		this.OriFileName = OriFileName;
	}

	public String getOriFileType() {
		return OriFileType;
	}

	public void setOriFileType(String OriFileType) {
		this.OriFileType = OriFileType;
	}

	public String getOriFilePath() {
		return OriFilePath;
	}

	public void setOriFilePath(String OriFilePath) {
		this.OriFilePath = OriFilePath;
	}

	public String getOriMessage() {
		return OriMessage;
	}

	public void setOriMessage(String OriMessage) {
		this.OriMessage = OriMessage;
	}

	public String getTargetFilePath() {
		return TargetFilePath;
	}

	public void setTargetFilePath(String TargetFilePath) {
		this.TargetFilePath = TargetFilePath;
	}

	public String getPDFFileName() {
		return PDFFileName;
	}

	public void setPDFFileName(String PDFFileName) {
		this.PDFFileName = PDFFileName;
	}

	public int getPDFPageCount() {
		return PDFPageCount;
	}

	public void setPadding_value(int PDFPageCount) {
		this.PDFPageCount = PDFPageCount;
	}

	public String getPDFFilePath() {
		return PDFFilePath;
	}

	public void setPDFFilePath(String PDFFilePath) {
		this.PDFFilePath = PDFFilePath;
	}

	public String getGenDateTime() {
		return GenDateTime;
	}

	public void setGenDateTime(String GenDateTime) {
		this.GenDateTime = GenDateTime;
	}
	
	public String getPDFFileSize() {
		return PDFFileSize;
	}

	public void setPDFFileSize(String PDFFileSize) {
		this.PDFFileSize = PDFFileSize;
	}
}
