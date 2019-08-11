package servent.message;

import app.ServentInfo;

public class PauseMessage extends BasicMessage {

	private static final long serialVersionUID = -9075856317699777975L;

	private int N;

	public PauseMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, int N) {
		super(MessageType.PAUSE, originalSenderInfo, receiverInfo, "Pause " + N);
		this.N = N;
	}

	public int getN() {
		return N;
	}

}
