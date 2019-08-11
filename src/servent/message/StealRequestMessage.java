package servent.message;

import app.ServentInfo;
import threadSafeObj.Token;

public class StealRequestMessage extends BasicMessage {

	private static final long serialVersionUID = 1565400864L;

	private int limit;

	public StealRequestMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, int limit) {
		super(MessageType.STEAL_REQUEST, originalSenderInfo, receiverInfo, "GIVE ME UR WORK");

		this.limit = limit;
	}

	public int getLimit() {
		return limit;
	}
}
