package threadSafeObj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import app.ServentInfo;

public class Token implements Serializable{


	private static final long serialVersionUID = 1454496849L;
	
	private ArrayList<ServentInfo> queue = new ArrayList<ServentInfo>();

	public Token() {
	}
	
	public Token(Token t) {
		this.queue = new ArrayList<ServentInfo>(t.queue);
	}

	public void addToQueue(ServentInfo requestor) {
		try {

			for (ServentInfo ser : queue) {
				if (ser.equals(requestor)) {
					return;
				}
			}

			queue.add(requestor);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public ServentInfo queueNext() {
		ServentInfo next = null;
		try {

			if (!queue.isEmpty())
				next = queue.remove(0);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return next;
	}

}
