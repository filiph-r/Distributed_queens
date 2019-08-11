package app;

import calculation.Calculator;
import cli.CLIParser;
import servent.SimpleServentListener;
import servent.message.BasicMessage;
import servent.message.MessageType;
import servent.message.util.MessageUtil;

public class ServentMain {

	public static void main(String[] args) {
		if (args.length != 1) {
			AppConfig.timestampedErrorPrint("Please provide konfiguration file");
		}

		int serventId = -1;
		int portNumber = -1;

		String configurationFile = args[0];

		AppConfig.readConfig(configurationFile);

		AppConfig.timestampedStandardPrint("Starting servent " + AppConfig.myServentInfo);

		// connection listener
		SimpleServentListener simpleListener = new SimpleServentListener();
		Thread listenerThread = new Thread(simpleListener);
		listenerThread.start();

		joinCluster();

		Calculator calculator = new Calculator();
		Thread calculatorThread = new Thread(calculator);
		calculatorThread.start();

		CLIParser cliParser = new CLIParser(simpleListener, calculator);
		Thread cliThread = new Thread(cliParser);
		cliThread.start();

	}

	static private void joinCluster() {
		ServentInfo receiverInfo = new ServentInfo(AppConfig.myServentInfo.getBootStrapIpAdress(),
				AppConfig.myServentInfo.getBootStrapPort(), "", 0000, 1);

		MessageUtil.sendMessage(
				new BasicMessage(MessageType.JOIN, AppConfig.myServentInfo, receiverInfo, "I WANT TO JOIN"));
	}
}
