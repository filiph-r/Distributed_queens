package cli.command;

import java.util.concurrent.ConcurrentLinkedQueue;

import app.AppConfig;
import app.ServentInfo;
import calculation.Calculator;
import cli.CLIParser;
import servent.SimpleServentListener;
import servent.message.BasicMessage;
import servent.message.MessageType;
import servent.message.util.MessageUtil;

public class StatusCommand implements CLICommand {

	public StatusCommand() {

	}

	@Override
	public String commandName() {
		return "status";
	}

	@Override
	public void execute(String args) {

		AppConfig.statusCount.set(AppConfig.myServentInfo.neighbors.size());
		AppConfig.statusList = new ConcurrentLinkedQueue<>();
		AppConfig.statusList.addAll(AppConfig.finalResults.allStatusEntries());

		for (ServentInfo n : AppConfig.myServentInfo.neighbors) {
			BasicMessage msg = new BasicMessage(MessageType.REQUEST_STATUS, AppConfig.myServentInfo, n,
					"GIVE ME UR STATUS");
			MessageUtil.sendMessage(msg);
		}

	}

}
