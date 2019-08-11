package app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MultipleServentStarter {

	private static class ServentCLI implements Runnable {

		private List<Process> serventProcesses;

		public ServentCLI(List<Process> serventProcesses) {
			this.serventProcesses = serventProcesses;
		}

		@Override
		public void run() {
			Scanner sc = new Scanner(System.in);

			while (true) {
				String line = sc.nextLine();

				if (line.equals("stop")) {
					for (Process process : serventProcesses) {
						process.destroy();
					}
					break;
				}
			}

			sc.close();
		}
	}

	private static void startServent() {
		List<Process> serventProcesses = new ArrayList<>();
		AppConfig.timestampedStandardPrint("Starting bootstrap server");

		ProcessBuilder buildr = new ProcessBuilder("java", "-cp", "bin/", "app.BootStrap");
		buildr.redirectOutput(new File("properties/bootstrap/bootstrap_out.txt"));
		buildr.redirectError(new File("properties/bootstrap/bootstrap_err.txt"));
		buildr.redirectInput(new File("properties/bootstrap/bootstrap_in.txt"));

		try {
			Process proc = buildr.start();
			serventProcesses.add(proc);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		AppConfig.timestampedStandardPrint("Starting multiple servent runner. "
				+ "If servents do not finish on their own, type \"stop\" to finish them");

		int serventCount = new File("properties/nodes").listFiles().length;

		for (int i = 0; i < serventCount; i++) {
			try {
				ProcessBuilder builder = new ProcessBuilder("java", "-cp", "bin/", "app.ServentMain",
						"properties/nodes/node" + (i + 1));

				// We use files to read and write.
				// System.out, System.err and System.in will point to these files.
				builder.redirectOutput(new File("properties/output/servent" + (i + 1) + "_out.txt"));
				builder.redirectError(new File("properties/error/servent" + (i + 1) + "_err.txt"));
				builder.redirectInput(new File("properties/input/servent" + (i + 1) + "_in.txt"));

				// Starts the servent as a completely separate process.
				Process p = builder.start();
				serventProcesses.add(p);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Thread t = new Thread(new ServentCLI(serventProcesses));

		t.start(); // CLI thread waiting for user to type "stop".

		for (Process process : serventProcesses) {
			try {
				process.waitFor(); // Wait for graceful process finish.
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		AppConfig.timestampedStandardPrint("All servent processes finished. Type \"stop\" to exit.");
	}

	public static void main(String[] args) {
		startServent();

	}

}
