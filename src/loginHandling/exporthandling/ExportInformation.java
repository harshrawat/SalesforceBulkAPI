package loginHandling.exporthandling;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ExportInformation")
public class ExportInformation {
	
	private String objectName;
	private String soql;
	private String fileLocation;
	private String fileName;
	private String contentType;

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public ExportInformation(String objectName, String soql, String fileLocation, String fileName, String contentType) {
		super();
		this.objectName = objectName;
		this.soql = soql;
		this.fileLocation = fileLocation;
		this.fileName = fileName;
		this.contentType = contentType;
	}

	public ExportInformation() {
		super();
	}

	public String getSoql() {
		return soql;
	}

	public void setSoql(String soql) {
		this.soql = soql;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public String getFilePath(){
		return getFileLocation()+getFileName();
	}
}
