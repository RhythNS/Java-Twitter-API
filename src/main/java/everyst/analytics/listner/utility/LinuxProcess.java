package everyst.analytics.listner.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import everyst.analytics.listner.dataManagement.Logger;

public class LinuxProcess {

	public static String execute(String... command) throws IOException, InterruptedException {
		// make a new process with the twurl command
		Process procScript = new ProcessBuilder(command).start(); // change 3000 up

		// wait for the program to finish
		procScript.waitFor();

		// read everything from the output of the program
		InputStream inputStream = procScript.getInputStream();
		String result = new BufferedReader(new InputStreamReader(inputStream)).lines()
				.collect(Collectors.joining("\n"));

		try {
			inputStream.close();
		} catch (IOException e) {
			Logger.getInstance().handleError(e);
		}
		return result;
	}
	
}
