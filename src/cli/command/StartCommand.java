package cli.command;

import java.util.ArrayList;

import app.AppConfig;
import app.ServentInfo;
import calculation.Job;
import servent.message.BasicMessage;
import servent.message.JobChunkMessage;
import servent.message.MessageType;
import servent.message.PauseMessage;
import servent.message.ResumeMessage;
import servent.message.TokenMessage;
import servent.message.util.MessageUtil;

public class StartCommand implements CLICommand {

	@Override
	public String commandName() {
		return "start";
	}

	@Override
	public void execute(String args) {

		int X = Integer.parseInt(args);
		if (AppConfig.finalResults.hasKey(X)) {
			if (AppConfig.finalResults.getStatus(X).equals("Active"))
				AppConfig.timestampedErrorPrint("Calculation for " + X + " already started!");

			if (AppConfig.finalResults.getStatus(X).equals("Done"))
				AppConfig.timestampedErrorPrint("Calculation for " + X + " already done!");

			if (AppConfig.finalResults.getStatus(X).equals("Paused")) {
				workExecute(args);
			}
		} else {
			AppConfig.finalResults.setKey(X);
			workExecute(args);
		}

	}

	private void workExecute(String args) {
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
		criticalSection(args);
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

	private void criticalSection(String args) {
		AppConfig.timestampedStandardPrint("Starting calculation for " + args);

		int N = Integer.parseInt(args);
		AppConfig.finalResults.setStatus(N, "Active");

		// ako imamo u pausedJobs neke job-ove za N
		// onda njih samo prebacujemo u jobQueue i saljemo svima RESUME message
		ArrayList<Job> NJobs = new ArrayList<Job>();
		for (Job j : AppConfig.pausedJobs) {
			if (j.N == N) {
				NJobs.add(j);
			}
		}
		AppConfig.pausedJobs.removeAll(NJobs);
		synchronized (AppConfig.jobQueueKey) {
			AppConfig.jobQueue.addAll(NJobs);
		}

		sendResumeToAll(N);
		if (!NJobs.isEmpty()) {
			return;
		}

		ArrayList<Job> jobs = new ArrayList<>();
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				Job job = new Job(N, i, j);
				jobs.add(job);
			}
		}

		int neighbours = AppConfig.myServentInfo.neighbors.size();
		int chunkSize = jobs.size() / (neighbours + 1);

		// System.out.println("NEIGHBOURS: " + neighbours);
		// System.out.println("JOB SIZE: " + jobs.size());
		// System.out.println("CHUNK SIZE: " + chunkSize);

		for (int i = 0; i < neighbours; i++) {
			ArrayList<Job> jobChunk = new ArrayList<>();

			for (int j = 0; j < chunkSize; j++) {
				jobChunk.add(jobs.remove(0));
			}

			// saljemo i-tom komsiji jobChunk
			JobChunkMessage jobMsg = new JobChunkMessage(AppConfig.myServentInfo,
					AppConfig.myServentInfo.neighbors.get(i), jobChunk);
			MessageUtil.sendMessage(jobMsg);
		}

		// System.out.println("JOBS: " + jobs.size());
		synchronized (AppConfig.jobQueueKey) {
			AppConfig.jobQueue.addAll(jobs);

			ArrayList<Integer> jobN = new ArrayList<Integer>();
			ArrayList<Job> jobList = new ArrayList<Job>();
			AppConfig.jobQueue.drainTo(jobList);

			for (Job j : jobList) {
				if (!jobN.contains(j.N)) {
					jobN.add(j.N);
				}
			}

			// stavljamo u queue da se racuna trenutno N <active>
			jobN.remove((Integer) N);
			for (Job j : jobList) {
				if (j.N == N) {
					AppConfig.jobQueue.add(j);
				}
			}

			// stavljamo u pausedJobs listu sve ostale N <paused>
			for (Integer n : jobN) {
				for (Job j : jobList) {
					if (j.N == n) {
						AppConfig.pausedJobs.add(j);
						AppConfig.finalResults.setStatus(j.N, "Paused");
					}
				}
				sendPauseToAll(n);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sendResumeToAll(N);

		}

	}

	private void sendRequestToAll() {
		for (ServentInfo n : AppConfig.myServentInfo.neighbors) {
			BasicMessage msg = new BasicMessage(MessageType.REQUEST_TOKEN, AppConfig.myServentInfo, n, "Request Token");
			MessageUtil.sendMessage(msg);
		}
	}

	private void sendResumeToAll(int N) {
		for (ServentInfo n : AppConfig.myServentInfo.neighbors) {
			ResumeMessage resumeMsg = new ResumeMessage(AppConfig.myServentInfo, n, N);
			MessageUtil.sendMessage(resumeMsg);
		}
	}

	private void sendPauseToAll(int N) {
		for (ServentInfo n : AppConfig.myServentInfo.neighbors) {
			PauseMessage pauseMsg = new PauseMessage(AppConfig.myServentInfo, n, N);
			MessageUtil.sendMessage(pauseMsg);
		}
	}
}
