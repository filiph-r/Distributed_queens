package servent.message.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import app.AppConfig;
import app.ServentInfo;
import servent.BootstrapListener;
import servent.message.Message;
import servent.message.MessageType;

public class DelayedMessageSender implements Runnable {

	private Message messageToSend;

	public DelayedMessageSender(Message messageToSend) {
		this.messageToSend = messageToSend;
	}

	public void run() {
		/*
		 * A random sleep before sending. It is important to take regular naps for
		 * health reasons.
		 */
		try {
			Thread.sleep((long) (Math.random() * 1000) + 500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		ServentInfo receiverInfo = messageToSend.getReceiverInfo();

		if (MessageUtil.MESSAGE_UTIL_PRINTING) {
			if (messageToSend.getMessageType() != MessageType.STEAL_REQUEST
					&& messageToSend.getMessageType() != MessageType.STEAL_RESPONSE)
				AppConfig.timestampedStandardPrint("Sending message " + messageToSend);
		}

		try {

			Socket sendSocket = new Socket(receiverInfo.getIpAddress(), receiverInfo.getListenerPort());

			ObjectOutputStream oos = new ObjectOutputStream(sendSocket.getOutputStream());
			oos.writeObject(messageToSend);
			oos.flush();

			sendSocket.close();

			if (messageToSend.getMessageType() == MessageType.JOIN_RESPONSE) {
				messageToSend.sendEffect(BootstrapListener.serventList);
			}

		} catch (IOException e) {
			AppConfig.timestampedErrorPrint("Couldn't send message: " + messageToSend.toString());
			AppConfig.myServentInfo.neighbors.remove(messageToSend.getOriginalSenderInfo());
			// e.printStackTrace();
		}
	}

}
