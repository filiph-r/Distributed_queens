package servent.message;

import app.ServentInfo;
import threadSafeObj.Token;

public class TokenMessage extends BasicMessage {

	private static final long serialVersionUID = 1156549864L;

	private Token token;

	public TokenMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo, String messageText,
			Token token) {
		super(type, originalSenderInfo, receiverInfo, messageText);
		this.token = token;
	}

	public Token getToken() {
		return token;
	}

}
