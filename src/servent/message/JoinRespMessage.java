package servent.message;

import java.util.ArrayList;
import java.util.List;

import app.ServentInfo;
import threadSafeObj.Token;

public class JoinRespMessage extends BasicMessage {

	private static final long serialVersionUID = 1L;

	private List<ServentInfo> neighbors = new ArrayList<>();
	private Token token;

	public JoinRespMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo,
			String messageText, List<ServentInfo> neighbors, Token token) {
		super(type, originalSenderInfo, receiverInfo, messageText);
		this.neighbors = neighbors;
		this.token = token;
	}

	public List<ServentInfo> getNeighbors() {
		return neighbors;
	}

	public Token getToken() {
		return token;
	}

	@Override
	public void sendEffect(List<ServentInfo> list) {
		list.add(getReceiverInfo());
	}

}
