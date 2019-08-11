package servent.message;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import app.ServentInfo;
import calculation.Job;
import threadSafeObj.Token;

public class LeavingMessage extends BasicMessage {

	private static final long serialVersionUID = 1656549864L;

	private ArrayList<Job> jobQueue;
	private ArrayList<Job> pausedJobs;
	private Token token;

	public LeavingMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, BlockingQueue<Job> jobQueue,
			ConcurrentLinkedQueue<Job> pausedJobs, Token token) {
		super(MessageType.LEAVING, originalSenderInfo, receiverInfo, "I'm leaving");

		this.jobQueue = new ArrayList<Job>();
		if (jobQueue != null)
			jobQueue.drainTo(this.jobQueue);

		this.pausedJobs = new ArrayList<Job>();
		if (pausedJobs != null)
			this.pausedJobs.addAll(pausedJobs);

		this.token = token;
	}

	public ArrayList<Job> getJobQueue() {
		return jobQueue;
	}

	public ArrayList<Job> getPausedJobs() {
		return pausedJobs;
	}

	public Token getToken() {
		return token;
	}

}
