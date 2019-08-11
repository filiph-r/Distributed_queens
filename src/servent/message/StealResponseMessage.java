package servent.message;

import java.util.ArrayList;

import app.ServentInfo;
import calculation.Job;

public class StealResponseMessage extends BasicMessage {

	private static final long serialVersionUID = 1156549862L;

	private ArrayList<Job> jobQueue;

	public StealResponseMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, ArrayList<Job> jobQueue) {
		super(MessageType.STEAL_RESPONSE, originalSenderInfo, receiverInfo, "TAKE IT");

		this.jobQueue = jobQueue;
	}

	public ArrayList<Job> getJobQueue() {
		return jobQueue;
	}

}
