package loginHandling;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import loginHandling.exporthandling.ExportInformation;
import loginHandling.importhandling.ImportInformation;
import loginHandling.userinformation.UserInformation;

@XmlRootElement(name = "Information")
public class ReadActionInformation {

	private UserInformation userinformation;
	private ExportInformation exportInformation;
	private ImportInformation importInformation;
	private String action;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public UserInformation getUserinformation() {
		return userinformation;
	}

	public void setUserinformation(UserInformation userinformation) {
		this.userinformation = userinformation;
	}

	public ExportInformation getExportInformation() {
		return exportInformation;
	}

	public void setExportInformation(ExportInformation exportInformation) {
		this.exportInformation = exportInformation;
	}

	public ImportInformation getImportInformation() {
		return importInformation;
	}

	public void setImportInformation(ImportInformation importInformation) {
		this.importInformation = importInformation;
	}

	public ReadActionInformation(String fileName) {
		File file = new File(fileName);
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ReadActionInformation.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			ReadActionInformation readActionInfo = (ReadActionInformation) jaxbUnmarshaller.unmarshal(file);
			this.setAction(readActionInfo.getAction());
			this.setUserinformation(readActionInfo.getUserinformation());
			this.setExportInformation(readActionInfo.getExportInformation());
			this.setImportInformation(readActionInfo.getImportInformation());
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	public ReadActionInformation(){}
}
