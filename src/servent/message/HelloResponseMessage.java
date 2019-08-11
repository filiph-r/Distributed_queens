package servent.message;

import app.ServentInfo;
import threadSafeObj.FinalResults;

public class HelloResponseMessage extends BasicMessage {

	private static final long serialVersionUID = 1156849864L;

	private FinalResults finalResults;

	public HelloResponseMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo, FinalResults finalResults) {
		super(MessageType.HELLO_REPONSE, originalSenderInfo, receiverInfo, "Hello response");

		this.finalResults = finalResults;
	}

	public FinalResults getFinalResults() {
		return finalResults;
	}

}
