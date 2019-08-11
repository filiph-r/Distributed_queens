package threadSafeObj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import calculation.AllResults;
import calculation.CalculatorUtils;
import calculation.OneResult;

public class FinalResults implements Serializable {

	public final Lock lock = new ReentrantLock();

	private HashMap<Integer, ArrayList<int[][]>> results = new HashMap<>();
	private HashMap<Integer, Integer> reportedResults = new HashMap<>();
	private HashMap<Integer, String> status = new HashMap<Integer, String>();
	private int active = 0;

	public void insert(AllResults allRez) {
		lock.lock();
		try {
			reportedResults.merge(allRez.N, allRez.results.size(), (oldValue, newValue) -> {
				return oldValue + newValue;
			});

			if (results.get(allRez.N) == null) {
				results.put(allRez.N, new ArrayList<int[][]>());
			}

			ArrayList<int[][]> list = results.get(allRez.N);
			for (OneResult r : allRez.results) {
				list.addAll(r.results);
			}

			list = CalculatorUtils.filterDuplicates(list, allRez.N);
			results.put(allRez.N, list);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public int statusPercent(int N) {

		lock.lock();
		int stat = 0;

		try {
			double reports = reportedResults.get(N);
			double maxReports = N * N;

			double decStat = reports / maxReports;
			decStat = decStat * 100;

			stat = (int) decStat;

		} catch (Exception e) {

		} finally {
			lock.unlock();
		}

		if (stat > 100) {
			stat = 100;
		}
		return stat;
	}

	public String getResult(int N) {
		lock.lock();
		StringBuilder sb = new StringBuilder();
		sb.append("\n");

		try {
			ArrayList<int[][]> list = results.get(N);
			for (int[][] board : list) {
				for (int i = 0; i < N; i++) {
					for (int j = 0; j < N; j++) {
						if (board[i][j] == 1) {
							sb.append(" Q ");
						} else {
							sb.append(" _ ");
						}
					}
					sb.append("\n");
				}
				sb.append("-----------------------------------------\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		return sb.toString();
	}

	public boolean hasKey(int key) {
		lock.lock();
		boolean rez = false;

		try {
			rez = results.containsKey(key);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		return rez;
	}

	public void setKey(int key) {
		lock.lock();

		try {
			results.put(key, new ArrayList<int[][]>());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

	}

	public void setStatus(Integer N, String status) {
		lock.lock();

		try {
			if (status.equals("Active")) {
				if (active != 0) {
					if (this.status.get(active).equals("Active"))
						this.status.put(active, "Paused");
				}

				this.status.put(N, status);
				active = N;
			} else {
				this.status.put(N, status);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

	}

	public String getStatus(Integer N) {
		lock.lock();
		String resp = "";

		try {

			resp = status.get(N);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		return resp;
	}

	public String allStatus() {
		lock.lock();
		String resp = "";

		try {

			Iterator<Entry<Integer, String>> it = status.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				Integer key = (Integer) pair.getKey();
				String value = (String) pair.getValue();

				resp += pair.toString() + " || ";

				// it.remove();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		return resp;
	}

	public ArrayList<Entry<Integer, String>> allStatusEntries() {
		lock.lock();
		ArrayList<Entry<Integer, String>> resp = new ArrayList<Map.Entry<Integer, String>>();

		try {

			Iterator<Entry<Integer, String>> it = status.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				Integer key = (Integer) pair.getKey();
				String value = (String) pair.getValue();

				resp.add(pair);

				// it.remove();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		return resp;
	}

}
