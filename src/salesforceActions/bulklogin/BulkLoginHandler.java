package salesforceActions.bulklogin;

import java.io.FileNotFoundException;

import com.sforce.async.AsyncApiException;
import com.sforce.async.BulkConnection;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

import loginHandling.userinformation.UserInformation;

public class BulkLoginHandler {

	private BulkConnection bulkConnection;

	public BulkConnection getBulkConnection() {
		return bulkConnection;
	}

	public void setBulkConnection(BulkConnection bulkConnection) {
		this.bulkConnection = bulkConnection;
	}

	public BulkLoginHandler(UserInformation userInfo) throws ConnectionException, AsyncApiException {
		createBulkConnectoin(userInfo.getUsername(), userInfo.getPassword()+userInfo.getSecurityToken(), userInfo.getOrgtype());
	}

	private ConnectorConfig bulkLoginConfiguration(String userName, String password, String environment)
			throws FileNotFoundException {
		ConnectorConfig configuration = new ConnectorConfig();
		configuration.setUsername(userName);
		configuration.setPassword(password);
		System.out.println("BulkAPI.java file environment: " + environment);
		if (environment.trim().equalsIgnoreCase("Production") || environment.trim().equalsIgnoreCase("Developer")) {
			configuration.setAuthEndpoint("https://login.salesforce.com/services/Soap/u/39.0");
		} else if (environment.trim().equalsIgnoreCase("Sandbox")) {
			configuration.setAuthEndpoint("https://test.salesforce.com/services/Soap/u/39.0");
		} else {
			configuration.setAuthEndpoint("https://login.salesforce.com/services/Soap/u/26.0");
		}
		configuration.setCompression(true);
		configuration.setTraceFile("traceLogs.txt");
		configuration.setTraceMessage(true);
		configuration.setPrettyPrintXml(true);
		return configuration;
	}

	private void createBulkConnectoin(String userName, String password, String environment)
			throws ConnectionException, AsyncApiException {
		try {
			ConnectorConfig configuration = bulkLoginConfiguration(userName, password, environment);
			new PartnerConnection(configuration);

			String soapEndpoint = configuration.getServiceEndpoint();
			String apiVersion = "39.0";
			String restEndpoint = soapEndpoint.substring(0, soapEndpoint.indexOf("Soap/")) + "async/" + apiVersion;
			configuration.setRestEndpoint(restEndpoint);
			configuration.setCompression(true);
			bulkConnection = new BulkConnection(configuration);

		} catch (AsyncApiException aae) {
			aae.printStackTrace();
		} catch (ConnectionException ce) {
			ce.printStackTrace();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
	}

}
