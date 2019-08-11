package threadSafeObj;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import app.ServentInfo;
import calculation.AllResults;

public class TokenHolder {

	private final Lock lock = new ReentrantLock();
	private Token token = null;

	public boolean hasToken() {
		lock.lock();
		boolean resp = false;
		try {

			if (token != null) {
				resp = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		return resp;
	}

	public Token releaseToken() {
		lock.lock();

		Token resp = null;
		try {

			resp = new Token(token);
			token = null;

		} catch (Exception e) {
			//e.printStackTrace();
			resp = null;
		} finally {
			lock.unlock();
		}

		return resp;
	}

	public void setToken(Token token) {
		lock.lock();

		try {

			this.token = token;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

	}

	public void addToTokenQueue(ServentInfo servent) {
		lock.lock();

		try {

			if (token != null) {
				token.addToQueue(servent);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

	}

	public ServentInfo getNextFromTokenQueue() {
		lock.lock();
		ServentInfo resp = null;

		try {

			resp = token.queueNext();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		return resp;
	}

}
