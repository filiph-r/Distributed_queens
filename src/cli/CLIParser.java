package cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import app.AppConfig;
import app.Cancellable;
import calculation.Calculator;
import cli.command.CLICommand;
import cli.command.InfoCommand;
import cli.command.PauseCommand;
import cli.command.SleepCommand;
import cli.command.ResultCommand;
import cli.command.StartCommand;
import cli.command.StatusCommand;
import cli.command.StopCommand;
import servent.SimpleServentListener;

public class CLIParser implements Runnable, Cancellable {

	private volatile boolean working = true;

	private final List<CLICommand> commandList;

	public CLIParser(SimpleServentListener listener, Calculator calculator) {
		this.commandList = new ArrayList<>();

		commandList.add(new InfoCommand());
		commandList.add(new SleepCommand());
		commandList.add(new StopCommand(this, listener, calculator));
		commandList.add(new StartCommand());
		commandList.add(new ResultCommand());
		commandList.add(new PauseCommand());
		commandList.add(new StatusCommand());
	}

	@Override
	public void run() {
		Scanner sc = new Scanner(System.in);

		while (working) {
			String commandLine = sc.nextLine();

			int spacePos = commandLine.indexOf(" ");

			String commandName = null;
			String commandArgs = null;
			if (spacePos != -1) {
				commandName = commandLine.substring(0, spacePos);
				commandArgs = commandLine.substring(spacePos + 1, commandLine.length());
			} else {
				commandName = commandLine;
			}

			boolean found = false;

			for (CLICommand cliCommand : commandList) {
				if (cliCommand.commandName().equals(commandName)) {
					cliCommand.execute(commandArgs);
					found = true;
					break;
				}
			}

			if (!found) {
				AppConfig.timestampedErrorPrint("Unknown command: " + commandName);
			}
		}

		sc.close();
	}

	@Override
	public void stop() {
		this.working = false;

	}
}
