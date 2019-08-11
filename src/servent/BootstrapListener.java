package servent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.AppConfig;
import app.Cancellable;
import app.ServentInfo;
import servent.message.JoinRespMessage;
import servent.message.LeavingMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;
import threadSafeObj.Token;

public class BootstrapListener implements Runnable, Cancellable {

	public static List<ServentInfo> serventList = new ArrayList<>();
	private volatile boolean working = true;
	private boolean first = true;

	private final int port;

	public BootstrapListener(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		ServerSocket listenerSocket = null;
		try {
			listenerSocket = new ServerSocket(port, 100);
			/*
			 * If there is no connection after 1s, wake up and see if we should terminate.
			 */
			listenerSocket.setSoTimeout(1000);
		} catch (IOException e) {
			AppConfig.timestampedErrorPrint(
					"Couldn't open listener socket on: " + AppConfig.myServentInfo.getListenerPort());
			System.exit(0);
		}

		while (working) {
			try {
				Message message;
				Socket clientSocket = listenerSocket.accept();

				message = MessageUtil.readMessage(clientSocket);

				if (message.getMessageType() == MessageType.JOIN) {
					JoinRespMessage joinMsg;

					// dajemo prvome samo token
					if (first) {
						joinMsg = new JoinRespMessage(MessageType.JOIN_RESPONSE, AppConfig.myServentInfo,
								message.getOriginalSenderInfo(), "GRANTED", serventList, new Token());
						first = false;
					} else {
						joinMsg = new JoinRespMessage(MessageType.JOIN_RESPONSE, AppConfig.myServentInfo,
								message.getOriginalSenderInfo(), "GRANTED", serventList, null);
					}

					MessageUtil.sendMessage(joinMsg);
				}

				if (message.getMessageType() == MessageType.LEAVING) {
					LeavingMessage leavingMsg = (LeavingMessage) message;

					for (int i = 0; i < serventList.size(); i++) {
						if (serventList.get(i).equals(leavingMsg.getOriginalSenderInfo())) {
							serventList.remove(i);
							break;
						}
					}

					if (serventList.isEmpty()) {
						first = true;
					}

				}

			} catch (SocketTimeoutException timeoutEx) {
				// Uncomment the next line to see that we are waking up every second.
				// AppConfig.timedStandardPrint("Waiting...");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void stop() {
		this.working = false;
	}

}
