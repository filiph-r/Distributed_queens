package servent.message.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;

import app.AppConfig;
import app.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;

public class MessageUtil {

	public static final boolean MESSAGE_UTIL_PRINTING = true;

	public static Message readMessage(Socket socket) {
		Message clientMessage = null;

		try {
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

			clientMessage = (Message) ois.readObject();

			socket.close();
		} catch (IOException e) {
			AppConfig.timestampedErrorPrint(
					"Error in reading socket on " + socket.getInetAddress() + ":" + socket.getPort());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		if (MESSAGE_UTIL_PRINTING) {
			if (clientMessage.getMessageType() != MessageType.STEAL_REQUEST
					&& clientMessage.getMessageType() != MessageType.STEAL_RESPONSE)
				AppConfig.timestampedStandardPrint("Got message " + clientMessage);
		}

		return clientMessage;
	}

	public static void sendMessage(Message message) {
		Thread delayedSender = new Thread(new DelayedMessageSender(message));

		delayedSender.start();
	}

	public static void sendMessage(Message message, List<ServentInfo> list) {
		Thread delayedSender = new Thread(new DelayedMessageSender(message));

		delayedSender.start();
	}
}
