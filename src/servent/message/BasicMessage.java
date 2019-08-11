package servent.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import app.AppConfig;
import app.ServentInfo;

public class BasicMessage implements Message {

	private static final long serialVersionUID = -9075856313609777945L;
	private MessageType type;
	private ServentInfo originalSenderInfo;
	private ServentInfo receiverInfo;
	private List<ServentInfo> routeList;
	private String messageText;

	// This gives us a unique id - incremented in every natural constructor.
	private AtomicInteger messageCounter = new AtomicInteger(0);
	private int messageId;

	public BasicMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo,
			String messageText) {
		super();
		this.type = type;
		this.originalSenderInfo = originalSenderInfo;
		this.receiverInfo = receiverInfo;
		this.messageText = messageText;
		this.messageId = messageCounter.incrementAndGet();
	}

	@Override
	public MessageType getMessageType() {
		return type;
	}

	@Override
	public ServentInfo getOriginalSenderInfo() {
		return originalSenderInfo;
	}

	@Override
	public ServentInfo getReceiverInfo() {
		return receiverInfo;
	}

	@Override
	public List<ServentInfo> getRoute() {
		return routeList;
	}

	@Override
	public String getMessageText() {
		return messageText;
	}

	@Override
	public int getMessageId() {
		return messageId;
	}

	protected BasicMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo,
			List<ServentInfo> routeList, String messageText, int messageId) {
		this.type = type;
		this.originalSenderInfo = originalSenderInfo;
		this.receiverInfo = receiverInfo;
		this.routeList = routeList;
		this.messageText = messageText;

		this.messageId = messageId;
	}

	/**
	 * Used when resending a message. It will not change the original owner (so
	 * equality is not affected), but will add us to the route list, so message path
	 * can be retraced later.
	 */
	@Override
	public Message makeMeASender() {
		ServentInfo newRouteItem = AppConfig.myServentInfo;

		List<ServentInfo> newRouteList = new ArrayList<>(routeList);
		newRouteList.add(newRouteItem);
		Message toReturn = new BasicMessage(getMessageType(), getOriginalSenderInfo(), getReceiverInfo(), newRouteList,
				getMessageText(), getMessageId());

		return toReturn;
	}

	/**
	 * Comparing messages is based on their unique id and the original sender id.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BasicMessage) {
			BasicMessage other = (BasicMessage) obj;

			if (getMessageId() == other.getMessageId()
					&& getOriginalSenderInfo().getIpAddress() == other.getOriginalSenderInfo().getIpAddress()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Hash needs to mirror equals, especially if we are gonna keep this object in a
	 * set or a map. So, this is based on message id and original sender id also.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getMessageId(), getOriginalSenderInfo().getIpAddress());
	}

	/**
	 * Returns the message in the format:
	 * <code>[sender_id|message_id|text|type|receiver_id]</code>
	 */
	@Override
	public String toString() {
		return "[" + getOriginalSenderInfo().getIpAddress() + "|" + getOriginalSenderInfo().getListenerPort() + "|"
				+ getMessageId() + "|" + getMessageText() + "|" + getMessageType() + "|"
				+ getReceiverInfo().getIpAddress() + "|" + getReceiverInfo().getListenerPort() + "]";
	}

	/**
	 * Empty implementation, which will be suitable for most messages.
	 */
	@Override
	public void sendEffect(List<ServentInfo> list) {

	}
}
