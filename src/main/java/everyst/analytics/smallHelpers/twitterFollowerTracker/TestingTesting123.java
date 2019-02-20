package everyst.analytics.smallHelpers.twitterFollowerTracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class TestingTesting123 {
	
	public static void main(String[] args) throws IOException, InterruptedException {
	    String[] cmdScript = new String[]{"/bin/bash", "-c", "twurl"}; 
	    Process procScript = Runtime.getRuntime().exec(cmdScript);
	    procScript.waitFor();
	    InputStream inputStream =  procScript.getInputStream();
	    String result = new BufferedReader(new InputStreamReader(inputStream))
	    		  .lines().collect(Collectors.joining("\n"));
	    System.out.println(result);
	    
	    
	}

}
