package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class Globals {
	
	private static String filename = "/log.txt";
	
	public static void log(String msg) {
		if (Constants.DEBUG) System.out.println(msg);
		
		String currentDir;
		File directory = new File ("");
		try {
		    currentDir = directory.getAbsolutePath();
		}catch(Exception e) {
		    System.err.println("Can't get current directory");
		    return;
		}
		
		Writer writer = null;
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(currentDir + filename), "utf-8"));
		    writer.write(msg + '\n');
		} catch (IOException ex) {
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {/*ignore*/}
		}
	}
	
	public static void logConstants() {
		log(Constants.DEBUG ? "Debug: true" : "Debug: false");
		//log(Constants.rewindGain)
		//TODO: finish this
	}
}
