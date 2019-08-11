package cli.command;

import app.AppConfig;
import app.ServentInfo;
import calculation.Calculator;
import cli.CLIParser;
import servent.SimpleServentListener;
import servent.message.LeavingMessage;
import servent.message.MessageType;
import servent.message.TokenMessage;
import servent.message.util.MessageUtil;

public class StopCommand implements CLICommand {

	private CLIParser parser;
	private SimpleServentListener listener;
	private Calculator calculator;

	public StopCommand(CLIParser parser, SimpleServentListener listener, Calculator calculator) {
		this.parser = parser;
		this.listener = listener;
		this.calculator = calculator;
	}

	@Override
	public String commandName() {
		return "stop";
	}

	@Override
	public void execute(String args) {
		AppConfig.timestampedStandardPrint("Stopping...");
		AppConfig.stoping = true;
		
		synchronized (AppConfig.jobQueueKey) {
			while (!AppConfig.resultMap.isEmpty()) {

			}
			sendLeavingToBootStrap();
			sendLeavingToNeighbours();

			parser.stop();
			listener.stop();
		}

		while (!AppConfig.resultMap.isEmpty()) {

		}

		calculator.stop();
	}

	private void sendLeavingToNeighbours() {

		boolean first = true;
		for (ServentInfo neighbour : AppConfig.myServentInfo.neighbors) {
			if (first) {
				LeavingMessage msg = new LeavingMessage(AppConfig.myServentInfo, neighbour, AppConfig.jobQueue,
						AppConfig.pausedJobs, AppConfig.tokenHolder.releaseToken());
				MessageUtil.sendMessage(msg);

				first = false;
			} else {
				LeavingMessage msg = new LeavingMessage(AppConfig.myServentInfo, neighbour, null, null,
						AppConfig.tokenHolder.releaseToken());
				MessageUtil.sendMessage(msg);
			}

		}

	}

	private void sendLeavingToBootStrap() {
		ServentInfo receiverInfo = new ServentInfo(AppConfig.myServentInfo.getBootStrapIpAdress(),
				AppConfig.myServentInfo.getBootStrapPort(), "", 0000, 1);

		LeavingMessage leavingMsg = new LeavingMessage(AppConfig.myServentInfo, receiverInfo, null, null, null);
		MessageUtil.sendMessage(leavingMsg);
	}

}
