package servent.message;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import app.ServentInfo;

public class StatusMessage extends BasicMessage {

	private static final long serialVersionUID = 1156541864L;

	private ArrayList<SimpleImmutableEntry<Integer, String>> status;

	public StatusMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo, String messageText,
			ArrayList<Map.Entry<Integer, String>> status) {
		super(type, originalSenderInfo, receiverInfo, messageText);
		this.status = new ArrayList<>();
		for (Map.Entry<Integer, String> s : status) {
			this.status.add(new SimpleImmutableEntry<Integer, String>(s.getKey(), s.getValue()));
		}

	}

	public ArrayList<SimpleImmutableEntry<Integer, String>> getStatus() {
		return status;
	}

}
