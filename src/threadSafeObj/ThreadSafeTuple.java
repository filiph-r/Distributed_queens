package threadSafeObj;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeTuple<X, Y> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final Lock lock = new ReentrantLock();
	public X x;
	public Y y;

	public ThreadSafeTuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	public X getX() {
		lock.lock();
		X retVal = null;

		try {
			retVal = x;
		} catch (Exception e) {
		} finally {
			lock.unlock();
		}

		return retVal;
	}

	public ThreadSafeTuple<X, Y> get() {
		lock.lock();
		ThreadSafeTuple<X, Y> retVal = null;

		try {
			retVal = new ThreadSafeTuple<X, Y>(x, y);
		} catch (Exception e) {
		} finally {
			lock.unlock();
		}

		return retVal;
	}

	public Y getY() {
		lock.lock();
		Y retVal = null;

		try {
			retVal = y;
		} catch (Exception e) {
		} finally {
			lock.unlock();
		}

		return retVal;
	}

	public boolean equalsTuple(X x, Y y) {
		lock.lock();
		boolean retVal = false;

		try {
			if (this.x == x && this.y == y) {
				retVal = true;
			}
		} catch (Exception e) {
		} finally {
			lock.unlock();
		}

		return retVal;
	}

	public void setTuple(X x, Y y) {
		lock.lock();

		try {
			this.x = x;
			this.y = y;
		} catch (Exception e) {
		} finally {
			lock.unlock();
		}

	}

	@Override
	public String toString() {
		return "ThreadSafeTuple [x=" + x + ", y=" + y + "]";
	}

}
