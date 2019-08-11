package app;

import java.util.Scanner;

import cli.CLIParser;
import servent.BootstrapListener;

public class BootStrap {

	public static void main(String[] args) {

		ServentInfo myServant = new ServentInfo("127.0.0.1", 8888, "", 0, 0);
		AppConfig.myServentInfo = myServant;

		BootstrapListener bListener = new BootstrapListener(8888);
		Thread thread = new Thread(bListener);
		thread.start();

		try {
			Scanner sc = new Scanner(System.in);
			while (true) {
				String line = sc.nextLine();
				if (line.equals("pause")) {
					Thread.sleep(5000);
				}
				if (line.equals("stop")) {
					bListener.stop();
					break;
				}
			}

			sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
