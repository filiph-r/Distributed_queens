package servent.message;

import java.io.Serializable;
import java.util.List;

import app.ServentInfo;


public interface Message extends Serializable {


	ServentInfo getOriginalSenderInfo();

	List<ServentInfo> getRoute();

	ServentInfo getReceiverInfo();

	MessageType getMessageType();

	String getMessageText();

	int getMessageId();

	Message makeMeASender();

	void sendEffect(List<ServentInfo> list);

}
