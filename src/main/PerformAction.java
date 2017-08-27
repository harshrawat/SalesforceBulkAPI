package main;

import loginHandling.ReadActionInformation;
import salesforceActions.bulkimport.BulkImport;
import salesforceActions.bulkretrive.BulkRetrive;

public class PerformAction {

	public static void main(String args[]){
		if(args.length <= 0){
			System.out.println("Please provide the file name");
		}else{
			ReadActionInformation readActionInformation = new ReadActionInformation(args[0]);
			if(readActionInformation.getAction().equalsIgnoreCase("Export")){
				BulkRetrive bulkExport = new BulkRetrive();
				bulkExport.retriveSoqlFromSalesforce(readActionInformation.getUserinformation(), readActionInformation.getExportInformation());
				bulkExport.saveResponse(readActionInformation.getExportInformation().getFilePath());
			}else if(readActionInformation.getAction().equalsIgnoreCase("Import")){
				BulkImport bulkImport = new BulkImport();
				bulkImport.importInformationInSalesforce(readActionInformation.getUserinformation(), readActionInformation.getImportInformation());
			}else{
				System.out.println("Please define action as Export or Import");
			}
		}
	}
	
}
