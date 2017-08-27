package salesforceActions.bulkimport;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sforce.async.AsyncApiException;
import com.sforce.async.BatchInfo;
import com.sforce.async.BatchStateEnum;
import com.sforce.async.BulkConnection;
import com.sforce.async.JobInfo;
import com.sforce.ws.ConnectionException;

import loginHandling.importhandling.ImportInformation;
import loginHandling.userinformation.UserInformation;
import salesforceActions.bulklogin.BulkLoginHandler;
import salesforceActions.jobhandler.JobHandler;

public class BulkImport {

	public void importInformationInSalesforce(UserInformation userInfo, ImportInformation impoInfo) {

		try {
			BulkLoginHandler salesforceLogin = new BulkLoginHandler(userInfo);
			JobHandler salesforceJobs = new JobHandler(salesforceLogin.getBulkConnection(), impoInfo);

			List<BatchInfo> batchInfos = new ArrayList<BatchInfo>();
			BufferedReader rdr = new BufferedReader(new InputStreamReader(new FileInputStream(impoInfo.getFilePath())));

			File tmpFile = File.createTempFile("bulkAPIInsert", ".csv");

			importBatchHandling(tmpFile, batchInfos, salesforceLogin.getBulkConnection(),
					salesforceJobs.getSalesforceJob(), rdr);

			salesforceJobs.closeJob(salesforceLogin.getBulkConnection());

			awaitCompletion(salesforceLogin.getBulkConnection(), salesforceJobs.getSalesforceJob(), batchInfos);
			checkResults(salesforceLogin.getBulkConnection(), salesforceJobs.getSalesforceJob(), batchInfos,impoInfo.getResponseFilePath());
		} catch (AsyncApiException | ConnectionException | IOException e) {
			e.printStackTrace();
		}

	}

	private void importBatchHandling(File tmpFile, List<BatchInfo> batchInfos, BulkConnection connection,
			JobInfo jobInfo, BufferedReader rdr) {
		try {
			FileOutputStream tmpOut = new FileOutputStream(tmpFile);
			int maxBytesPerBatch = 10000000; // 10 million bytes per batch
			int maxRowsPerBatch = 10000; // 10 thousand rows per batch
			int currentBytes = 0;
			int currentLines = 0;
			String nextLine;
			byte[] headerBytes = (rdr.readLine() + "\n").getBytes("UTF-8");
			int headerBytesLength = headerBytes.length;
			while ((nextLine = rdr.readLine()) != null) {
				byte[] bytes = (nextLine + "\n").getBytes("UTF-8");
				// Create a new batch when our batch size limit is reached
				if (currentBytes + bytes.length > maxBytesPerBatch || currentLines > maxRowsPerBatch) {
					createBatch(tmpOut, tmpFile, batchInfos, connection, jobInfo);
					currentBytes = 0;
					currentLines = 0;
				}
				if (currentBytes == 0) {
					tmpOut = new FileOutputStream(tmpFile);
					tmpOut.write(headerBytes);
					currentBytes = headerBytesLength;
					currentLines = 1;
				}
				tmpOut.write(bytes);
				currentBytes += bytes.length;
				currentLines++;
			}
			// Finished processing all rows
			// Create a final batch for any remaining data
			if (currentLines > 1) {
				createBatch(tmpOut, tmpFile, batchInfos, connection, jobInfo);
			}
			tmpOut.close();
			tmpFile.delete();
		} catch (IOException | AsyncApiException ex) {
			ex.printStackTrace();
		}
	}

	public List<BatchInfo> createBatchesFromCSVFile(BulkConnection connection, JobInfo jobInfo, String csvFileName)
			throws IOException, AsyncApiException {
		List<BatchInfo> batchInfos = new ArrayList<BatchInfo>();
		BufferedReader rdr = new BufferedReader(new InputStreamReader(new FileInputStream(csvFileName)));
		// read the CSV header row
		byte[] headerBytes = (rdr.readLine() + "\n").getBytes("UTF-8");
		int headerBytesLength = headerBytes.length;
		File tmpFile = File.createTempFile("bulkAPIInsert", ".csv");

		// Split the CSV file into multiple batches
		try {
			FileOutputStream tmpOut = new FileOutputStream(tmpFile);
			int maxBytesPerBatch = 10000000; // 10 million bytes per batch
			int maxRowsPerBatch = 10000; // 10 thousand rows per batch
			int currentBytes = 0;
			int currentLines = 0;
			String nextLine;
			while ((nextLine = rdr.readLine()) != null) {
				byte[] bytes = (nextLine + "\n").getBytes("UTF-8");
				// Create a new batch when our batch size limit is reached
				if (currentBytes + bytes.length > maxBytesPerBatch || currentLines > maxRowsPerBatch) {
					createBatch(tmpOut, tmpFile, batchInfos, connection, jobInfo);
					currentBytes = 0;
					currentLines = 0;
				}
				if (currentBytes == 0) {
					tmpOut = new FileOutputStream(tmpFile);
					tmpOut.write(headerBytes);
					currentBytes = headerBytesLength;
					currentLines = 1;
				}
				tmpOut.write(bytes);
				currentBytes += bytes.length;
				currentLines++;
			}
			// Finished processing all rows
			// Create a final batch for any remaining data
			if (currentLines > 1) {
				createBatch(tmpOut, tmpFile, batchInfos, connection, jobInfo);
			}
		} finally {
			tmpFile.delete();
			rdr.close();
		}
		return batchInfos;
	}

	private void createBatch(FileOutputStream tmpOut, File tmpFile, List<BatchInfo> batchInfos,
			BulkConnection connection, JobInfo jobInfo) throws IOException, AsyncApiException {
		tmpOut.flush();
		tmpOut.close();
		FileInputStream tmpInputStream = new FileInputStream(tmpFile);
		try {
			BatchInfo batchInfo = connection.createBatchFromStream(jobInfo, tmpInputStream);
			System.out.println(batchInfo);
			batchInfos.add(batchInfo);
		} catch (Exception ex) {
			System.out.println("##### Exception in createbatch " + ex);
		} finally {
			tmpInputStream.close();
		}
	}

	private void awaitCompletion(BulkConnection connection, JobInfo job, List<BatchInfo> batchInfoList)
			throws AsyncApiException {
		long sleepTime = 0L;
		Set<String> incomplete = new HashSet<String>();
		for (BatchInfo bi : batchInfoList) {
			incomplete.add(bi.getId());
		}
		while (!incomplete.isEmpty()) {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
			}
			System.out.println("Awaiting results..." + incomplete.size());
			sleepTime = 10000L;
			BatchInfo[] statusList = connection.getBatchInfoList(job.getId()).getBatchInfo();
			for (BatchInfo b : statusList) {
				if (b.getState() == BatchStateEnum.Completed || b.getState() == BatchStateEnum.Failed) {
					if (incomplete.remove(b.getId())) {
						System.out.println("BATCH STATUS:\n" + b);
					}
				}
			}
		}
	}

	private void checkResults(BulkConnection connection, JobInfo job, List<BatchInfo> batchInfoList,String responsePath)
			throws AsyncApiException, IOException {
		int count = 0;
		for (BatchInfo b : batchInfoList) {
			saveResponse((ByteArrayInputStream) connection.getBatchResultStream(job.getId(), b.getId()), responsePath,
					count != 0);
			count++;
		}
	}

	private void saveResponse(ByteArrayInputStream inputStream, String fileName, boolean append) {
		if (inputStream != null) {
			try (PrintWriter out = new PrintWriter(new FileOutputStream(new File(fileName), append))) {
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
