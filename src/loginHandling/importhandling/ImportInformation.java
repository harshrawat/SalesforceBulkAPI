package loginHandling.importhandling;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ImportInformation")
public class ImportInformation {

	private String objectName;
	private String fileLocation;
	private String fileName;
	private String contentType;
	private String operation;
	private String externalIdFieldName;

	public String getExternalIdFieldName() {
		return externalIdFieldName;
	}

	public void setExternalIdFieldName(String externalIdFieldName) {
		this.externalIdFieldName = externalIdFieldName;
	}

	public ImportInformation(String objectName, String fileLocation, String fileName, String contentType,
			String operation, String externalIdFieldName) {
		super();
		this.objectName = objectName;
		this.fileLocation = fileLocation;
		this.fileName = fileName;
		this.contentType = contentType;
		this.operation = operation;
		this.externalIdFieldName = externalIdFieldName;
	}

	public ImportInformation() {
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
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
	
	public String getResponseFilePath(){
		return getFileLocation()+"Response.csv";
	}

}
