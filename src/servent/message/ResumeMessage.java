package servent.message;

import java.util.ArrayList;

import app.ServentInfo;
import calculation.AllResults;
import calculation.Job;

public class ResumeMessage extends BasicMessage {

	private static final long serialVersionUID = -9075856313699777975L;

	private int N;

	public ResumeMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, int N) {
		super(MessageType.RESUME, originalSenderInfo, receiverInfo, "Resume " + N);
		this.N = N;
	}

	public int getN() {
		return N;
	}

}
