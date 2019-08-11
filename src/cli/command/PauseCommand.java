package cli.command;

import java.util.ArrayList;

import app.AppConfig;
import app.ServentInfo;
import calculation.Calculator;
import calculation.Job;
import cli.CLIParser;
import servent.SimpleServentListener;
import servent.message.BasicMessage;
import servent.message.MessageType;
import servent.message.PauseMessage;
import servent.message.TokenMessage;
import servent.message.util.MessageUtil;

public class PauseCommand implements CLICommand {

	public PauseCommand() {

	}

	@Override
	public String commandName() {
		return "pause";
	}

	@Override
	public void execute(String args) {

		workExecute();

	}

	private void workExecute() {
		AppConfig.worksWithToken = true;

		if (!AppConfig.tokenHolder.hasToken()) {

			// povecavamo nas brojac jer broadcastujemo request
			AppConfig.myServentInfo.sn.getAndIncrement();
			while (AppConfig.tokenHolder.hasToken() == false) {
				// saljemo vise puta u slucaju da je poslat request izmedju slanja i primanja
				// tokena
				sendRequestToAll();

				// cekamo
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		int[] LN = new int[AppConfig.myServentInfo.neighbors.size()];
		for (int i = 0; i < LN.length; i++) {
			LN[i] = AppConfig.myServentInfo.neighbors.get(i).sn.get();
		}

		// -------------------------------------------------------------------
		criticalSection();
		// -------------------------------------------------------------------

		for (int i = 0; i < LN.length; i++) {
			if (AppConfig.myServentInfo.neighbors.get(i).sn.get() == LN[i] + 1) {
				// dodajemo u queue
				AppConfig.tokenHolder.addToTokenQueue(AppConfig.myServentInfo.neighbors.get(i));
			}
		}

		ServentInfo nextSevent = AppConfig.tokenHolder.getNextFromTokenQueue();
		if (nextSevent != null) {
			TokenMessage tokenMsg = new TokenMessage(MessageType.TOKEN, AppConfig.myServentInfo, nextSevent,
					"Take token", AppConfig.tokenHolder.releaseToken());
			MessageUtil.sendMessage(tokenMsg);
		}

		AppConfig.worksWithToken = false;
	}

	public void criticalSection() {

		synchronized (AppConfig.jobQueueKey) {

			ArrayList<Integer> jobN = new ArrayList<Integer>();
			ArrayList<Job> jobList = new ArrayList<Job>();
			AppConfig.jobQueue.drainTo(jobList);

			for (Job j : jobList) {
				if (!jobN.contains(j.N)) {
					jobN.add(j.N);
				}
			}

			// stavljamo u pausedJobs listu sve N <paused>
			for (Integer n : jobN) {
				for (Job j : jobList) {
					if (j.N == n) {
						AppConfig.pausedJobs.add(j);
						AppConfig.finalResults.setStatus(j.N, "Active");
						AppConfig.finalResults.setStatus(j.N, "Paused");
					}
				}
				sendPauseToAll(n);
			}

		}

	}

	private void sendRequestToAll() {
		for (ServentInfo n : AppConfig.myServentInfo.neighbors) {
			BasicMessage msg = new BasicMessage(MessageType.REQUEST_TOKEN, AppConfig.myServentInfo, n, "Request Token");
			MessageUtil.sendMessage(msg);
		}
	}

	private void sendPauseToAll(int N) {
		for (ServentInfo n : AppConfig.myServentInfo.neighbors) {
			PauseMessage pauseMsg = new PauseMessage(AppConfig.myServentInfo, n, N);
			MessageUtil.sendMessage(pauseMsg);
		}
	}
}
