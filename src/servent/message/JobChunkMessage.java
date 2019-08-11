package servent.message;

import java.util.ArrayList;

import app.ServentInfo;
import calculation.AllResults;
import calculation.Job;

public class JobChunkMessage extends BasicMessage {

	private static final long serialVersionUID = -9075856313609777975L;

	private ArrayList<Job> jobChunk;

	public JobChunkMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, ArrayList<Job> jobChunk) {
		super(MessageType.JOB, originalSenderInfo, receiverInfo, "Job");
		this.jobChunk = jobChunk;
	}

	public ArrayList<Job> getJobChunk() {
		return jobChunk;
	}

}
