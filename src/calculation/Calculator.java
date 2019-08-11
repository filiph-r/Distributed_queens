package calculation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

import app.AppConfig;
import app.Cancellable;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;
import servent.message.ResultMessage;
import servent.message.StealRequestMessage;
import servent.message.util.MessageUtil;

public class Calculator implements Runnable, Cancellable {

	private boolean working = true;

	public Calculator() {
	}

	@Override
	public void run() {

		while (working) {

			try {
				if (AppConfig.stoping) {
					throw new NoSuchElementException();
				}

				Job job;
				synchronized (AppConfig.jobQueueKey) {
					job = AppConfig.jobQueue.remove();
				}

				AppConfig.timestampedStandardPrint("Started calculation for one part of " + job.N);

				Job backupJob = new Job(job.N, job.collumn, job.row);
				job.calculate();

				if (job.stoped) {
					AppConfig.jobQueue.add(backupJob);
				}
				if (job.aborted) {
					AppConfig.pausedJobs.add(backupJob);
				}

			} catch (NoSuchElementException e) {

				if (AppConfig.stoping == false) {
					if (AppConfig.stealing) {
						continue;
					}

					// work
					// stealing---------------------------------------------------------------------------------------
					synchronized (AppConfig.stealingKey) {

						// ako smo sve komsije pokrali i nema vise sta da se krade saljemo rezultat
						if (AppConfig.myServentInfo.neighbors.size() == AppConfig.victims.size()) {
							AppConfig.victims.clear();
						} else {
							AppConfig.stealing = true;
							while (true) {
								Random rand = new Random();
								int vicIndex = rand.nextInt(AppConfig.myServentInfo.neighbors.size());
								ServentInfo victim = AppConfig.myServentInfo.neighbors.get(vicIndex);

								if (AppConfig.victims.contains(victim) == false) {
									AppConfig.victims.add(victim);

									StealRequestMessage stealReq = new StealRequestMessage(AppConfig.myServentInfo,
											victim, AppConfig.myServentInfo.getLimit());

									MessageUtil.sendMessage(stealReq);
									break;
								}
							}
							continue;
						}

					}
					// -------------------------------------------------------------------------------------------------------
				}
				if (AppConfig.resultMap.isEmpty()) {
					continue;
				}

				Iterator<Entry<Integer, ArrayList<OneResult>>> it = AppConfig.resultMap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();

					AllResults allResults = new AllResults((ArrayList<OneResult>) pair.getValue(), (int) pair.getKey());

					// saljemo komsijama rezultat
					for (ServentInfo neighbour : AppConfig.myServentInfo.neighbors) {

						ResultMessage resMsg = new ResultMessage(AppConfig.myServentInfo, neighbour, allResults);
						MessageUtil.sendMessage(resMsg);

					}

					// sebi zapisujemo ono sto smo izracunali
					AppConfig.finalResults.insert(allResults);

					if (AppConfig.finalResults.statusPercent(allResults.N) == 100) {
						AppConfig.finalResults.setStatus(allResults.N, "Done");
					}

					it.remove();
				}

				AppConfig.resultMap.clear();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void stop() {
		working = false;
	}

}
