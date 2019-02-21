package everyst.analytics.listner.test;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import everyst.analytics.listner.dataManagement.queueWriter.FileManager;
import everyst.analytics.listner.dataManagement.queueWriter.StringReader;
import everyst.analytics.listner.dataManagement.queueWriter.StringWriter;
import everyst.analytics.listner.dataManagement.queueWriter.Type;

public class StringWriterTest {

	public static void main(String[] args) throws InterruptedException {
		boolean write = true, read = true;
	
		BlockingQueue<String> msqQueue = new LinkedBlockingQueue<>(999999999);
		
		FileManager fileManager = new FileManager(new File("TestingTesting"));
		StringWriter stringWriter = new StringWriter(fileManager);
	
		StringReader stringReader = new StringReader(fileManager, stringWriter, msqQueue);
		
		if (write) {
			stringWriter.write("Hello", Type.ERROR);
			stringWriter.write("Testing", Type.DEBUG);

			for (int i = 0; i < 300; i++) {
				stringWriter.write(Double.toString((Math.random() * 99999999)), Type.DEBUG);
			}
			for (int i = 0; i < 4000; i++) {
				stringWriter.write(Double.toString(Math.random() * 9999999), Type.ERROR);
			}
		}
		
		if (read) {
			stringReader.addAllStrings(Type.DEBUG, 0);
			System.out.println(msqQueue.size());
			Thread.sleep(2000);
			while (!msqQueue.isEmpty())
				System.out.println(msqQueue.take());
		}

	}

}
