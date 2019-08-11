package cli.command;

import app.AppConfig;

public class ResultCommand implements CLICommand {

	@Override
	public String commandName() {
		return "result";
	}

	@Override
	public void execute(String args) {
		try {
			int N = Integer.parseInt(args);

			if (AppConfig.finalResults.hasKey(N)) {
				if (AppConfig.finalResults.statusPercent(N) < 100) {
					AppConfig.timestampedErrorPrint(
							"Calculation for " + N + " not finished " + AppConfig.finalResults.statusPercent(N) + "%");
				} else {
					AppConfig.timestampedStandardPrint("\nRESULT FOR " + N + ":" + AppConfig.finalResults.getResult(N));
				}

			} else {
				AppConfig.timestampedErrorPrint("Calculation for " + N + " not started");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
