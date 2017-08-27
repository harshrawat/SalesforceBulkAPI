package salesforceActions.jobhandler;

import com.sforce.async.AsyncApiException;
import com.sforce.async.BulkConnection;
import com.sforce.async.ConcurrencyMode;
import com.sforce.async.ContentType;
import com.sforce.async.JobInfo;
import com.sforce.async.JobStateEnum;
import com.sforce.async.OperationEnum;

import loginHandling.exporthandling.ExportInformation;
import loginHandling.importhandling.ImportInformation;

public class JobHandler {

	private JobInfo salesforceJob;

	public JobInfo getSalesforceJob() {
		return salesforceJob;
	}

	public void setSalesforceJob(JobInfo salesforceJob) {
		this.salesforceJob = salesforceJob;
	}

	public JobHandler(BulkConnection bulkconnection, ExportInformation expoInfo) throws AsyncApiException {
		createSalesforceJob(bulkconnection, expoInfo.getObjectName(), "query", expoInfo.getContentType(), null);
	}

	public JobHandler(BulkConnection bulkconnection, ImportInformation impoInfo) throws AsyncApiException {
		createSalesforceJob(bulkconnection, impoInfo.getObjectName(), impoInfo.getOperation(),
				impoInfo.getContentType(), impoInfo.getExternalIdFieldName());
	}

	private void createSalesforceJob(BulkConnection bulkconnection, String sobjectName, String operation,
			String contentType, String externalId) throws AsyncApiException {
		JobInfo job = new JobInfo();
		job.setObject(sobjectName);
		job.setOperation(OperationEnum.valueOf(operation));
		job.setConcurrencyMode(ConcurrencyMode.Parallel);
		job.setContentType(ContentType.valueOf(contentType.toUpperCase()));
		if (externalId != null) {
			job.setExternalIdFieldName(externalId);
		}
		job = bulkconnection.createJob(job);
		job = bulkconnection.getJobStatus(job.getId());
		salesforceJob = job;
	}

	public void closeJob(BulkConnection connection) throws AsyncApiException {
		JobInfo job = new JobInfo();
		job.setId(salesforceJob.getId());
		job.setState(JobStateEnum.Closed);
		connection.updateJob(job);
	}

}
