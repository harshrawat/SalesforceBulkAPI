package salesforceActions.bulkretrive;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import com.sforce.async.AsyncApiException;
import com.sforce.async.BatchInfo;
import com.sforce.async.BatchStateEnum;
import com.sforce.async.QueryResultList;
import com.sforce.ws.ConnectionException;

import loginHandling.exporthandling.ExportInformation;
import loginHandling.userinformation.UserInformation;
import salesforceActions.bulklogin.BulkLoginHandler;
import salesforceActions.jobhandler.JobHandler;

public class BulkRetrive {

	public BulkRetrive() {
		this.inputStream = null;
	}

	private ByteArrayInputStream inputStream;

	public ByteArrayInputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(ByteArrayInputStream inputStream) {
		this.inputStream = inputStream;
	}

	public void retriveSoqlFromSalesforce(UserInformation userInfo, ExportInformation expoInfo) {
		try {
			BulkLoginHandler salesforceLogin = new BulkLoginHandler(userInfo);
			JobHandler salesforceJobs = new JobHandler(salesforceLogin.getBulkConnection(), expoInfo);
			ByteArrayInputStream soqlByteStream = new ByteArrayInputStream(expoInfo.getSoql().getBytes());
			BatchInfo batchInformation = salesforceLogin.getBulkConnection()
					.createBatchFromStream(salesforceJobs.getSalesforceJob(), soqlByteStream);

			String[] queryResults = null;
			QueryResultList list = null;
			int count = 0;
			for (int i = 0; i < 10000; i++) {
				count++;
				Thread.sleep(i == 0 ? 30 * 1000 : 30 * 1000); // 30 sec

				batchInformation = salesforceLogin.getBulkConnection()
						.getBatchInfo(salesforceJobs.getSalesforceJob().getId(), batchInformation.getId());
				// If Batch Status is Completed,get QueryResultList and store in
				// queryResults.
				if (batchInformation.getState() == BatchStateEnum.Completed) {
					list = salesforceLogin.getBulkConnection()
							.getQueryResultList(salesforceJobs.getSalesforceJob().getId(), batchInformation.getId());
					queryResults = list.getResult();

					break;
				} else if (batchInformation.getState() == BatchStateEnum.Failed) {
					System.out.println("-------------- failed ----------" + batchInformation);
					break;
				} else {
					System.out.println("-------------- waiting ----------" + batchInformation);
				}
			}
			System.out.println("count::--" + count);
			System.out.println("QueryResultList::" + list.toString());
			if (queryResults != null) {
				for (String resultId : queryResults) {
					inputStream = (ByteArrayInputStream) salesforceLogin.getBulkConnection().getQueryResultStream(
							salesforceJobs.getSalesforceJob().getId(), batchInformation.getId(), resultId);
				}
			}
			salesforceJobs.closeJob(salesforceLogin.getBulkConnection());
		} catch (ConnectionException | AsyncApiException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void saveResponse(String fileName){
		if(inputStream != null){
			try (PrintWriter out = new PrintWriter(fileName)) {
				int n = inputStream.available();
				byte[] bytes = new byte[n];
				inputStream.read(bytes, 0, n);
				out.println(new String(bytes, StandardCharsets.UTF_8));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
