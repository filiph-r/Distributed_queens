package servent;

import java.io.IOException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.AppConfig;
import app.Cancellable;
import app.ServentInfo;
import calculation.CalculatorUtils;
import calculation.Job;
import calculation.OneResult;
import servent.message.BasicMessage;
import servent.message.HelloResponseMessage;
import servent.message.JobChunkMessage;
import servent.message.JoinRespMessage;
import servent.message.LeavingMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PauseMessage;
import servent.message.ResultMessage;
import servent.message.ResumeMessage;
import servent.message.StatusMessage;
import servent.message.StealRequestMessage;
import servent.message.StealResponseMessage;
import servent.message.TokenMessage;
import servent.message.util.MessageUtil;

public class SimpleServentListener implements Runnable, Cancellable {

	private volatile boolean working = true;

	public SimpleServentListener() {
	}

	/*
	 * Thread pool for executing the handlers. Each client will get it's own handler
	 * thread.
	 */
	// private final ExecutorService threadPool = Executors.newWorkStealingPool();

	@Override
	public void run() {
		ServerSocket listenerSocket = null;
		try {
			listenerSocket = new ServerSocket(AppConfig.myServentInfo.getListenerPort(), 100);
			/*
			 * If there is no connection after 1s, wake up and see if we should terminate.
			 */
			listenerSocket.setSoTimeout(1000);
		} catch (IOException e) {
			AppConfig.timestampedErrorPrint(
					"Couldn't open listener socket on: " + AppConfig.myServentInfo.getListenerPort());
			System.exit(0);
		}

		while (working) {
			try {
				Message message;
				Socket clientSocket = listenerSocket.accept();

				message = MessageUtil.readMessage(clientSocket);
				if (message.getMessageType() == MessageType.JOIN_RESPONSE) {
					JoinRespMessage joinRespMsg = (JoinRespMessage) message;
					AppConfig.myServentInfo.neighbors = joinRespMsg.getNeighbors();
					AppConfig.tokenHolder.setToken(joinRespMsg.getToken());

					AppConfig.timestampedStandardPrint("NEIGBOUR UPDATE: ");
					for (ServentInfo neighbor : AppConfig.myServentInfo.neighbors) {
						AppConfig.timestampedStandardPrint("NEIGHBOR: " + neighbor.toString());
					}

					// javljamo se komsijama
					for (ServentInfo neighbor : AppConfig.myServentInfo.neighbors) {
						BasicMessage helloMsg = new BasicMessage(MessageType.HELLO_NEIGHBOUR, AppConfig.myServentInfo,
								neighbor, "Hello");
						MessageUtil.sendMessage(helloMsg);
					}
				}

				if (message.getMessageType() == MessageType.HELLO_NEIGHBOUR) {
					AppConfig.myServentInfo.neighbors.add(message.getOriginalSenderInfo());

					HelloResponseMessage respMsg = new HelloResponseMessage(AppConfig.myServentInfo,
							message.getOriginalSenderInfo(), AppConfig.finalResults);
					MessageUtil.sendMessage(respMsg);

					AppConfig.timestampedStandardPrint("NEIGHBOUR UPDATE: ");
					for (ServentInfo neighbor : AppConfig.myServentInfo.neighbors) {
						AppConfig.timestampedStandardPrint("NEIGHBOUR: " + neighbor.toString());
					}
				}

				if (message.getMessageType() == MessageType.JOB) {
					JobChunkMessage jobMsg = (JobChunkMessage) message;

					synchronized (AppConfig.jobQueueKey) {
						int N = jobMsg.getJobChunk().get(0).N;
						AppConfig.jobQueue.addAll(jobMsg.getJobChunk());

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
								AppConfig.finalResults.setStatus(j.N, "Active");
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
						}

					}
				}

				if (message.getMessageType() == MessageType.RESULT) {
					ResultMessage resMsg = (ResultMessage) message;

					// ovde ubacujemo u final rez
					AppConfig.finalResults.insert(resMsg.getAllResults());

					if (AppConfig.finalResults.statusPercent(resMsg.getAllResults().N) == 100) {
						AppConfig.finalResults.setStatus(resMsg.getAllResults().N, "Done");
					}

				}

				if (message.getMessageType() == MessageType.REQUEST_TOKEN) {
					BasicMessage requestMsg = (BasicMessage) message;
					ServentInfo sender = requestMsg.getOriginalSenderInfo();

					// zabelezavamo request
					for (ServentInfo neigbour : AppConfig.myServentInfo.neighbors) {
						if (sender.equals(neigbour)) {
							if (neigbour.sn.get() < sender.sn.get()) {
								neigbour.sn.getAndSet(sender.sn.get());
							}
						}
					}

					// ako imamo token i ne radimo s tokenom saljemo token
					// u suprotom ce se poslati kada zavrsi s kriticnom sekcijom
					if (AppConfig.tokenHolder.hasToken() && !AppConfig.worksWithToken) {
						TokenMessage tokenMsg = new TokenMessage(MessageType.TOKEN, AppConfig.myServentInfo, sender,
								"Take token", AppConfig.tokenHolder.releaseToken());
						MessageUtil.sendMessage(tokenMsg);
					}

				}

				if (message.getMessageType() == MessageType.TOKEN) {
					TokenMessage tokenMsg = (TokenMessage) message;
					if (tokenMsg.getToken() != null)
						AppConfig.tokenHolder.setToken(tokenMsg.getToken());
				}

				if (message.getMessageType() == MessageType.RESUME) {
					ResumeMessage resumeMsg = (ResumeMessage) message;
					int N = resumeMsg.getN();
					AppConfig.finalResults.setStatus(N, "Active");

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

				}

				if (message.getMessageType() == MessageType.PAUSE) {
					PauseMessage pauseMsg = (PauseMessage) message;
					int N = pauseMsg.getN();

					synchronized (AppConfig.jobQueueKey) {

						if (AppConfig.finalResults.getStatus(N).equals("Active")) {
							ArrayList<Integer> jobN = new ArrayList<Integer>();
							ArrayList<Job> jobList = new ArrayList<Job>();
							AppConfig.jobQueue.drainTo(jobList);

							for (Job j : jobList) {
								if (!jobN.contains(j.N)) {
									jobN.add(j.N);
								}
							}

							for (Integer n : jobN) {
								for (Job j : jobList) {
									if (j.N == n) {
										AppConfig.pausedJobs.add(j);
										AppConfig.finalResults.setStatus(j.N, "Active");
										AppConfig.finalResults.setStatus(j.N, "Paused");
									}
								}
							}
							AppConfig.finalResults.setStatus(N, "Paused");
						}
					}

				}

				if (message.getMessageType() == MessageType.REQUEST_STATUS) {
					BasicMessage msg = (BasicMessage) message;

					StatusMessage statusMsg = new StatusMessage(MessageType.STATUS, AppConfig.myServentInfo,
							msg.getOriginalSenderInfo(), "Status", AppConfig.finalResults.allStatusEntries());
					MessageUtil.sendMessage(statusMsg);
				}

				if (message.getMessageType() == MessageType.STATUS) {
					StatusMessage statusMsg = (StatusMessage) message;

					for (Entry<Integer, String> e : AppConfig.statusList) {
						try {
							for (Entry<Integer, String> en : statusMsg.getStatus()) {
								if (e.getKey().equals(en.getKey())) {
									if (!e.getValue().equals(en.getValue())) {
										AppConfig.statusList.remove(e);
										AppConfig.statusList
												.add(new SimpleImmutableEntry<Integer, String>(e.getKey(), "Fuzzy"));
									}
								}
							}
						} catch (Exception ex) {
						}
					}

					int count = AppConfig.statusCount.decrementAndGet();
					if (count == 0) {
						String str = "";
						for (Entry<Integer, String> e : AppConfig.statusList) {
							if (!e.getValue().equals("Done")) {
								str += e + " " + AppConfig.finalResults.statusPercent(e.getKey()) + "%  ||  ";
							} else {
								str += e + "  ||  ";
							}
						}
						AppConfig.timestampedStandardPrint(str);
					}

				}

				if (message.getMessageType() == MessageType.LEAVING) {

					LeavingMessage leavingMsg = (LeavingMessage) message;

					for (int i = 0; i < AppConfig.myServentInfo.neighbors.size(); i++) {
						if (AppConfig.myServentInfo.neighbors.get(i).equals(leavingMsg.getOriginalSenderInfo())) {
							AppConfig.myServentInfo.neighbors.remove(i);
							break;
						}
					}

					AppConfig.jobQueue.addAll(leavingMsg.getJobQueue());
					AppConfig.pausedJobs.addAll(leavingMsg.getPausedJobs());

					if (leavingMsg.getToken() != null) {
						AppConfig.tokenHolder.setToken(leavingMsg.getToken());
					}
				}

				if (message.getMessageType() == MessageType.HELLO_REPONSE) {
					HelloResponseMessage helloRespMsg = (HelloResponseMessage) message;

					AppConfig.finalResults = helloRespMsg.getFinalResults();
				}

				if (message.getMessageType() == MessageType.STEAL_RESPONSE) {
					synchronized (AppConfig.stealingKey) {
						StealResponseMessage stealRespMsg = (StealResponseMessage) message;

						// AppConfig.timestampedStandardPrint("STEALING!!!!!!!!!!!!!");
						if (stealRespMsg.getJobQueue() != null) {
							AppConfig.jobQueue.addAll(stealRespMsg.getJobQueue());
							// AppConfig.timestampedStandardPrint("STOLEN!!!!!!!!!!!!!");
						}

						AppConfig.stealing = false;
					}
				}

				if (message.getMessageType() == MessageType.STEAL_REQUEST) {
					StealRequestMessage stealReqMsg = (StealRequestMessage) message;
					int limit = stealReqMsg.getLimit();

					synchronized (AppConfig.jobQueueKey) {
						// AppConfig.timestampedStandardPrint("HELP ME!!!");
						if ((AppConfig.jobQueue.size() / 2) >= limit) {
							ArrayList<Job> halfJobs = new ArrayList<Job>();
							for (int i = 0; i < AppConfig.jobQueue.size() / 2; i++) {
								try {
									halfJobs.add(AppConfig.jobQueue.remove());
								} catch (Exception e) {
								}
							}

							StealResponseMessage stealResp = new StealResponseMessage(AppConfig.myServentInfo,
									stealReqMsg.getOriginalSenderInfo(), halfJobs);

							MessageUtil.sendMessage(stealResp);
						} else {
							StealResponseMessage stealResp = new StealResponseMessage(AppConfig.myServentInfo,
									stealReqMsg.getOriginalSenderInfo(), null);

							MessageUtil.sendMessage(stealResp);
						}

					}

				}

			} catch (SocketTimeoutException timeoutEx) {
				// Uncomment the next line to see that we are waking up every second.
				// AppConfig.timedStandardPrint("Waiting...");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void stop() {
		this.working = false;
	}

}
