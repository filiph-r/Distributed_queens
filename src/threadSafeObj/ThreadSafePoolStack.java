package threadSafeObj;

import java.util.Stack;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafePoolStack<E> {
	private final Stack<E> lista = new Stack<E>();;
	private final Lock lock = new ReentrantLock();

	public void push(E value) {
		lock.lock();

		try {
			lista.push(value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public E pop() {
		lock.lock();
		E retVal = null;

		try {

			retVal = lista.pop();

		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			lock.unlock();
		}

		return retVal;
	}

	public boolean isEmpty() {
		lock.lock();
		boolean retVal = true;

		try {

			retVal = lista.isEmpty();

		} catch (Exception e) {
			System.err.println("ThreadSafeStack: Nemoguce je pristupiti objektu");
		} finally {
			lock.unlock();
		}

		return retVal;
	}

	public void remove(int x) {
		lock.lock();

		try {
			lista.remove(x);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public E get(int x) {
		lock.lock();
		E retVal = null;

		try {

			retVal = lista.get(x);

		} catch (Exception e) {
			System.err.println("ThreadSafeStack: Nemoguce je pristupiti objektu pod indexom " + x);
		} finally {
			lock.unlock();
		}

		return retVal;
	}

	public int size() {
		lock.lock();
		int size = 0;

		try {
			size = lista.size();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		return size;
	}

}
