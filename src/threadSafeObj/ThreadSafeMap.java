package threadSafeObj;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeMap<K, V> {
	private Map<K, V> map;
	private final Lock lock = new ReentrantLock();

	public ThreadSafeMap() {
		this.map = new HashMap<K, V>();
	}

	public void put(K key, V value) {
		lock.lock();

		try {
			map.put(key, value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public void remove(K key) {
		lock.lock();

		try {
			map.remove(key);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public V get(K key) {
		lock.lock();
		V retVal = null;

		try {

			retVal = map.get(key);

		} catch (Exception e) {
			System.err.println("ThreadSafeMap: Nemoguce je pristupiti objektu");
		} finally {
			lock.unlock();
		}

		return retVal;
	}

	public boolean contains(K key) {
		lock.lock();
		boolean retVal = false;

		try {

			retVal = map.containsKey(key);

		} catch (Exception e) {
			System.err.println("ThreadSafeMap: Nemoguce je pristupiti objektu ");
		} finally {
			lock.unlock();
		}

		return retVal;
	}

	public int size() {
		lock.lock();
		int size = 0;

		try {
			size = map.size();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		return size;
	}

	public void clear() {
		lock.lock();

		try {
			map.clear();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public Set<K> keySet() {
		lock.lock();
		Set<K> result = null;

		try {
			result = map.keySet();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		return result;
	}

}
