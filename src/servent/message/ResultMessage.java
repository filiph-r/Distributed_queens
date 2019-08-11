package servent.message;

import app.ServentInfo;
import calculation.AllResults;

public class ResultMessage extends BasicMessage {

	private static final long serialVersionUID = -9075856313609777975L;

	private AllResults allResults;

	public ResultMessage(ServentInfo originalSenderInfo, ServentInfo receiverInfo,
			AllResults allResults) {
		super(MessageType.RESULT, originalSenderInfo, receiverInfo, "Result");
		this.allResults = allResults;
	}

	public AllResults getAllResults() {
		return allResults;
	}

}
